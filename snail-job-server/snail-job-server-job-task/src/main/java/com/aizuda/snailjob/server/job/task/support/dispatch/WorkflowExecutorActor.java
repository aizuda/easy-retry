package com.aizuda.snailjob.server.job.task.support.dispatch;

import akka.actor.AbstractActor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.aizuda.snailjob.common.core.constant.SystemConstants;
import com.aizuda.snailjob.common.core.context.SpringContext;
import com.aizuda.snailjob.common.core.enums.FailStrategyEnum;
import com.aizuda.snailjob.common.core.enums.JobOperationReasonEnum;
import com.aizuda.snailjob.common.core.enums.JobTaskBatchStatusEnum;
import com.aizuda.snailjob.common.core.util.JsonUtil;
import com.aizuda.snailjob.common.core.util.StreamUtils;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.server.common.akka.ActorGenerator;
import com.aizuda.snailjob.server.common.enums.SyetemTaskTypeEnum;
import com.aizuda.snailjob.server.common.exception.SnailJobServerException;
import com.aizuda.snailjob.server.common.util.DateUtils;
import com.aizuda.snailjob.server.job.task.dto.WorkflowNodeTaskExecuteDTO;
import com.aizuda.snailjob.server.job.task.support.WorkflowExecutor;
import com.aizuda.snailjob.server.job.task.support.WorkflowTaskConverter;
import com.aizuda.snailjob.server.job.task.support.alarm.event.WorkflowTaskFailAlarmEvent;
import com.aizuda.snailjob.server.job.task.support.cache.MutableGraphCache;
import com.aizuda.snailjob.server.job.task.support.executor.workflow.WorkflowExecutorContext;
import com.aizuda.snailjob.server.job.task.support.executor.workflow.WorkflowExecutorFactory;
import com.aizuda.snailjob.server.job.task.support.handler.WorkflowBatchHandler;
import com.aizuda.snailjob.server.job.task.support.timer.JobTimerWheel;
import com.aizuda.snailjob.server.job.task.support.timer.WorkflowTimeoutCheckTask;
import com.aizuda.snailjob.template.datasource.persistence.mapper.*;
import com.aizuda.snailjob.template.datasource.persistence.po.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import com.google.common.graph.MutableGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: xiaowoniu
 * @date : 2023-12-22 10:34
 * @since : 2.6.0
 */
@Component(ActorGenerator.WORKFLOW_EXECUTOR_ACTOR)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
@RequiredArgsConstructor
public class WorkflowExecutorActor extends AbstractActor {

    private final WorkflowTaskBatchMapper workflowTaskBatchMapper;
    private final WorkflowNodeMapper workflowNodeMapper;
    private final WorkflowMapper workflowMapper;
    private final JobMapper jobMapper;
    private final JobTaskBatchMapper jobTaskBatchMapper;
    private final WorkflowBatchHandler workflowBatchHandler;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(WorkflowNodeTaskExecuteDTO.class, taskExecute -> {
            log.debug("工作流开始执行. [{}]", JsonUtil.toJsonString(taskExecute));
            try {

                doExecutor(taskExecute);

            } catch (Exception e) {
                SnailJobLog.LOCAL.error("workflow executor exception. [{}]", taskExecute, e);
                handlerTaskBatch(taskExecute, JobTaskBatchStatusEnum.FAIL.getStatus(),
                        JobOperationReasonEnum.TASK_EXECUTION_ERROR.getReason());
                SpringContext.getContext().publishEvent(new WorkflowTaskFailAlarmEvent(taskExecute.getWorkflowTaskBatchId()));
            } finally {
                getContext().stop(getSelf());
            }
        }).build();
    }

    private void doExecutor(WorkflowNodeTaskExecuteDTO taskExecute) {
        WorkflowTaskBatch workflowTaskBatch = workflowTaskBatchMapper.selectById(taskExecute.getWorkflowTaskBatchId());
        Assert.notNull(workflowTaskBatch, () -> new SnailJobServerException("任务不存在"));

        if (SystemConstants.ROOT.equals(taskExecute.getParentId()) && JobTaskBatchStatusEnum.WAITING.getStatus() == workflowTaskBatch.getTaskBatchStatus()) {
            handlerTaskBatch(taskExecute, JobTaskBatchStatusEnum.RUNNING.getStatus(), JobOperationReasonEnum.NONE.getReason());

            Workflow workflow = workflowMapper.selectById(workflowTaskBatch.getWorkflowId());
            JobTimerWheel.clearCache(SyetemTaskTypeEnum.WORKFLOW.getType(), taskExecute.getWorkflowTaskBatchId());

            JobTimerWheel.registerWithWorkflow(() -> new WorkflowTimeoutCheckTask(taskExecute.getWorkflowTaskBatchId()),
                    Duration.ofSeconds(workflow.getExecutorTimeout()));
            // 超时检查
//            JobTimerWheel.register(SyetemTaskTypeEnum.WORKFLOW.getType(), taskExecute.getWorkflowTaskBatchId(),
//                    new WorkflowTimeoutCheckTask(taskExecute.getWorkflowTaskBatchId()), workflow.getExecutorTimeout(), TimeUnit.SECONDS);
        }

        // 获取DAG图
        String flowInfo = workflowTaskBatch.getFlowInfo();
        MutableGraph<Long> graph = MutableGraphCache.getOrDefault(workflowTaskBatch.getId(), flowInfo);

        Set<Long> successors = graph.successors(taskExecute.getParentId());
        if (CollUtil.isEmpty(successors)) {
            workflowBatchHandler.complete(taskExecute.getWorkflowTaskBatchId(), workflowTaskBatch);
            return;
        }

        Set<Long> brotherNode = MutableGraphCache.getBrotherNode(graph, taskExecute.getParentId());
        Sets.SetView<Long> union = Sets.union(successors, brotherNode);

        // 添加父节点，为了判断父节点的处理状态
        List<JobTaskBatch> allJobTaskBatchList = jobTaskBatchMapper.selectList(new LambdaQueryWrapper<JobTaskBatch>()
                .select(JobTaskBatch::getWorkflowTaskBatchId, JobTaskBatch::getWorkflowNodeId,
                        JobTaskBatch::getTaskBatchStatus, JobTaskBatch::getOperationReason)
                .eq(JobTaskBatch::getWorkflowTaskBatchId, workflowTaskBatch.getId())
                .in(JobTaskBatch::getWorkflowNodeId,
                        Sets.union(union, Sets.newHashSet(taskExecute.getParentId())))
        );

        List<WorkflowNode> workflowNodes = workflowNodeMapper.selectList(new LambdaQueryWrapper<WorkflowNode>()
                .in(WorkflowNode::getId, Sets.union(successors, Sets.newHashSet(taskExecute.getParentId())))
                .orderByAsc(WorkflowNode::getPriorityLevel));

        Map<Long, List<JobTaskBatch>> jobTaskBatchMap = StreamUtils.groupByKey(allJobTaskBatchList, JobTaskBatch::getWorkflowNodeId);
        Map<Long, WorkflowNode> workflowNodeMap = StreamUtils.toIdentityMap(workflowNodes, WorkflowNode::getId);
        List<JobTaskBatch> parentJobTaskBatchList = jobTaskBatchMap.get(taskExecute.getParentId());

        // 如果父节点是无需处理则不再继续执行
        if (CollUtil.isNotEmpty(parentJobTaskBatchList) &&
                parentJobTaskBatchList.stream()
                        .map(JobTaskBatch::getOperationReason)
                        .filter(Objects::nonNull)
                        .anyMatch(JobOperationReasonEnum.WORKFLOW_SUCCESSOR_SKIP_EXECUTION::contains)) {
            workflowBatchHandler.complete(taskExecute.getWorkflowTaskBatchId(), workflowTaskBatch);
            return;
        }

        // 失败策略处理
        if (CollUtil.isNotEmpty(parentJobTaskBatchList)
                && parentJobTaskBatchList.stream()
                .map(JobTaskBatch::getTaskBatchStatus)
                .anyMatch(i -> i != JobTaskBatchStatusEnum.SUCCESS.getStatus())) {
            // 判断是否继续处理，根据失败策略
            WorkflowNode workflowNode = workflowNodeMap.get(taskExecute.getParentId());
            // 失败了阻塞策略
            if (Objects.equals(workflowNode.getFailStrategy(), FailStrategyEnum.BLOCK.getCode())) {
                return;
            }
        }

        if (!brotherNodeIsComplete(taskExecute, brotherNode, jobTaskBatchMap)) {
            return;
        }

        // 去掉父节点
        workflowNodes = workflowNodes.stream()
                .filter(workflowNode -> !workflowNode.getId().equals(taskExecute.getParentId())).collect(
                        Collectors.toList());

        List<Job> jobs = jobMapper.selectBatchIds(StreamUtils.toSet(workflowNodes, WorkflowNode::getJobId));
        Map<Long, Job> jobMap = StreamUtils.toIdentityMap(jobs, Job::getId);

        // 只会条件节点会使用
        Object evaluationResult = null;
        for (WorkflowNode workflowNode : workflowNodes) {

            // 批次已经存在就不在重复生成
            List<JobTaskBatch> jobTaskBatchList = jobTaskBatchMap.get(workflowNode.getId());
            if (CollUtil.isNotEmpty(jobTaskBatchList)) {
                continue;
            }

            // 执行DAG中的节点
            WorkflowExecutor workflowExecutor = WorkflowExecutorFactory.getWorkflowExecutor(workflowNode.getNodeType());

            WorkflowExecutorContext context = WorkflowTaskConverter.INSTANCE.toWorkflowExecutorContext(workflowNode);
            context.setJob(jobMap.get(workflowNode.getJobId()));
            context.setWorkflowTaskBatchId(taskExecute.getWorkflowTaskBatchId());
            context.setParentWorkflowNodeId(taskExecute.getParentId());
            context.setEvaluationResult(evaluationResult);
            context.setTaskBatchId(taskExecute.getTaskBatchId());
            context.setTaskExecutorScene(taskExecute.getTaskExecutorScene());

            workflowExecutor.execute(context);

            evaluationResult = context.getEvaluationResult();
        }

    }

    private boolean brotherNodeIsComplete(WorkflowNodeTaskExecuteDTO taskExecute, Set<Long> brotherNode,
                                          Map<Long, List<JobTaskBatch>> jobTaskBatchMap) {

        if (SystemConstants.ROOT.equals(taskExecute.getParentId())) {
            return Boolean.TRUE;
        }

        // 判断所有节点是否都完成
        for (final Long nodeId : brotherNode) {
            List<JobTaskBatch> jobTaskBatches = jobTaskBatchMap.get(nodeId);
            // 说明此节点未执行, 继续等待执行完成
            if (CollUtil.isEmpty(jobTaskBatches)) {
                SnailJobLog.LOCAL.debug("存在未完成的兄弟节点. [{}]", nodeId);
                return Boolean.FALSE;
            }

            boolean isCompleted = jobTaskBatches.stream().anyMatch(
                    jobTaskBatch -> JobTaskBatchStatusEnum.NOT_COMPLETE.contains(jobTaskBatch.getTaskBatchStatus()));
            if (isCompleted) {
                SnailJobLog.LOCAL.debug("存在未完成的兄弟节点. [{}]", nodeId);
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    private void handlerTaskBatch(WorkflowNodeTaskExecuteDTO taskExecute, int taskStatus, int operationReason) {

        WorkflowTaskBatch jobTaskBatch = new WorkflowTaskBatch();
        jobTaskBatch.setId(taskExecute.getWorkflowTaskBatchId());
        jobTaskBatch.setExecutionAt(DateUtils.toNowMilli());
        jobTaskBatch.setTaskBatchStatus(taskStatus);
        jobTaskBatch.setOperationReason(operationReason);
        jobTaskBatch.setUpdateDt(LocalDateTime.now());
        Assert.isTrue(1 == workflowTaskBatchMapper.updateById(jobTaskBatch),
                () -> new SnailJobServerException("更新任务失败"));

    }

}

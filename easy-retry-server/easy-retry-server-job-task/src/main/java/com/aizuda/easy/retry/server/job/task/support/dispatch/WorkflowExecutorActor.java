package com.aizuda.easy.retry.server.job.task.support.dispatch;

import akka.actor.AbstractActor;
import cn.hutool.core.lang.Assert;
import com.aizuda.easy.retry.common.core.constant.SystemConstants;
import com.aizuda.easy.retry.common.core.enums.FailStrategyEnum;
import com.aizuda.easy.retry.common.core.enums.JobOperationReasonEnum;
import com.aizuda.easy.retry.common.core.enums.JobTaskBatchStatusEnum;
import com.aizuda.easy.retry.common.log.EasyRetryLog;
import com.aizuda.easy.retry.common.core.util.JsonUtil;
import com.aizuda.easy.retry.server.common.akka.ActorGenerator;
import com.aizuda.easy.retry.server.common.exception.EasyRetryServerException;
import com.aizuda.easy.retry.server.common.util.DateUtils;
import com.aizuda.easy.retry.server.job.task.dto.WorkflowNodeTaskExecuteDTO;
import com.aizuda.easy.retry.server.job.task.support.WorkflowExecutor;
import com.aizuda.easy.retry.server.job.task.support.WorkflowTaskConverter;
import com.aizuda.easy.retry.server.job.task.support.cache.MutableGraphCache;
import com.aizuda.easy.retry.server.job.task.support.executor.workflow.WorkflowExecutorContext;
import com.aizuda.easy.retry.server.job.task.support.executor.workflow.WorkflowExecutorFactory;
import com.aizuda.easy.retry.server.job.task.support.handler.WorkflowBatchHandler;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.JobMapper;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.JobTaskBatchMapper;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.WorkflowNodeMapper;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.WorkflowTaskBatchMapper;
import com.aizuda.easy.retry.template.datasource.persistence.po.Job;
import com.aizuda.easy.retry.template.datasource.persistence.po.JobTaskBatch;
import com.aizuda.easy.retry.template.datasource.persistence.po.WorkflowNode;
import com.aizuda.easy.retry.template.datasource.persistence.po.WorkflowTaskBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Sets;
import com.google.common.graph.MutableGraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
                EasyRetryLog.LOCAL.error("workflow executor exception. [{}]", taskExecute, e);
                handlerTaskBatch(taskExecute, JobTaskBatchStatusEnum.FAIL.getStatus(),
                    JobOperationReasonEnum.TASK_EXECUTION_ERROR.getReason());
                // TODO 发送通知
            } finally {
                getContext().stop(getSelf());
            }
        }).build();
    }

    private void doExecutor(WorkflowNodeTaskExecuteDTO taskExecute) {
        WorkflowTaskBatch workflowTaskBatch = workflowTaskBatchMapper.selectById(taskExecute.getWorkflowTaskBatchId());
        Assert.notNull(workflowTaskBatch, () -> new EasyRetryServerException("任务不存在"));

        if (SystemConstants.ROOT.equals(taskExecute.getParentId()) && JobTaskBatchStatusEnum.WAITING.getStatus() == workflowTaskBatch.getTaskBatchStatus()) {
            handlerTaskBatch(taskExecute, JobTaskBatchStatusEnum.RUNNING.getStatus(), JobOperationReasonEnum.NONE.getReason());
        }

        // 获取DAG图
        String flowInfo = workflowTaskBatch.getFlowInfo();
        MutableGraph<Long> graph = MutableGraphCache.getOrDefault(workflowTaskBatch.getId(), flowInfo);

        Set<Long> successors = graph.successors(taskExecute.getParentId());
        if (CollectionUtils.isEmpty(successors)) {
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

        Map<Long, List<JobTaskBatch>> jobTaskBatchMap = allJobTaskBatchList.stream()
            .collect(Collectors.groupingBy(JobTaskBatch::getWorkflowNodeId));
        Map<Long, WorkflowNode> workflowNodeMap = workflowNodes.stream()
            .collect(Collectors.toMap(WorkflowNode::getId, i -> i));
        List<JobTaskBatch> parentJobTaskBatchList = jobTaskBatchMap.get(taskExecute.getParentId());

        // 如果父节点是无需处理则不再继续执行
        if (!CollectionUtils.isEmpty(parentJobTaskBatchList) &&
            parentJobTaskBatchList.stream()
                .map(JobTaskBatch::getOperationReason)
                .filter(Objects::nonNull)
                .anyMatch(JobOperationReasonEnum.WORKFLOW_SUCCESSOR_SKIP_EXECUTION::contains)) {
            workflowBatchHandler.complete(taskExecute.getWorkflowTaskBatchId(), workflowTaskBatch);
            return;
        }

        // 失败策略处理
        if (!CollectionUtils.isEmpty(parentJobTaskBatchList)
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

        List<Job> jobs = jobMapper.selectBatchIds(
            workflowNodes.stream().map(WorkflowNode::getJobId).collect(Collectors.toSet()));
        Map<Long, Job> jobMap = jobs.stream().collect(Collectors.toMap(Job::getId, i -> i));

        // 只会条件节点会使用
        Object evaluationResult = null;
        for (WorkflowNode workflowNode : workflowNodes) {

            // 批次已经存在就不在重复生成
            List<JobTaskBatch> jobTaskBatchList = jobTaskBatchMap.get(workflowNode.getId());
            if (!CollectionUtils.isEmpty(jobTaskBatchList)) {
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
            if (CollectionUtils.isEmpty(jobTaskBatches)) {
                EasyRetryLog.LOCAL.debug("存在未完成的兄弟节点. [{}]", nodeId);
                return Boolean.FALSE;
            }

            boolean isCompleted = jobTaskBatches.stream().anyMatch(
                jobTaskBatch -> JobTaskBatchStatusEnum.NOT_COMPLETE.contains(jobTaskBatch.getTaskBatchStatus()));
            if (isCompleted) {
                EasyRetryLog.LOCAL.debug("存在未完成的兄弟节点. [{}]", nodeId);
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
            () -> new EasyRetryServerException("更新任务失败"));

    }

}

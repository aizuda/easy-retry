package com.aizuda.snailjob.server.job.task.support.handler;

import akka.actor.ActorRef;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import com.aizuda.snailjob.common.core.constant.SystemConstants;
import com.aizuda.snailjob.common.core.context.SpringContext;
import com.aizuda.snailjob.common.core.enums.JobOperationReasonEnum;
import com.aizuda.snailjob.common.core.enums.JobTaskBatchStatusEnum;
import com.aizuda.snailjob.common.core.enums.JobTaskStatusEnum;
import com.aizuda.snailjob.common.core.util.StreamUtils;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.server.common.akka.ActorGenerator;
import com.aizuda.snailjob.server.common.enums.JobTaskExecutorSceneEnum;
import com.aizuda.snailjob.server.common.exception.SnailJobServerException;
import com.aizuda.snailjob.server.common.util.DateUtils;
import com.aizuda.snailjob.server.job.task.dto.JobTaskPrepareDTO;
import com.aizuda.snailjob.server.job.task.dto.WorkflowNodeTaskExecuteDTO;
import com.aizuda.snailjob.server.job.task.support.JobTaskConverter;
import com.aizuda.snailjob.server.job.task.support.JobTaskStopHandler;
import com.aizuda.snailjob.server.job.task.support.alarm.event.WorkflowTaskFailAlarmEvent;
import com.aizuda.snailjob.server.job.task.support.cache.MutableGraphCache;
import com.aizuda.snailjob.server.job.task.support.stop.JobTaskStopFactory;
import com.aizuda.snailjob.server.job.task.support.stop.TaskStopJobContext;
import com.aizuda.snailjob.template.datasource.persistence.mapper.JobMapper;
import com.aizuda.snailjob.template.datasource.persistence.mapper.JobTaskBatchMapper;
import com.aizuda.snailjob.template.datasource.persistence.mapper.WorkflowTaskBatchMapper;
import com.aizuda.snailjob.template.datasource.persistence.po.Job;
import com.aizuda.snailjob.template.datasource.persistence.po.JobTaskBatch;
import com.aizuda.snailjob.template.datasource.persistence.po.WorkflowTaskBatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.graph.MutableGraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.*;

import static com.aizuda.snailjob.common.core.enums.JobOperationReasonEnum.WORKFLOW_SUCCESSOR_SKIP_EXECUTION;
import static com.aizuda.snailjob.common.core.enums.JobTaskBatchStatusEnum.NOT_COMPLETE;

/**
 * @author xiaowoniu
 * @date 2023-12-24 07:53:18
 * @since 2.6.0
 */
@Component
@RequiredArgsConstructor
public class WorkflowBatchHandler {

    private final WorkflowTaskBatchMapper workflowTaskBatchMapper;
    private final JobMapper jobMapper;
    private final JobTaskBatchMapper jobTaskBatchMapper;

    private static boolean checkLeafCompleted(MutableGraph<Long> graph, Map<Long,
            List<JobTaskBatch>> currentWorkflowNodeMap, Set<Long> parentIds) {

        // 判定子节点是否需要处理
        boolean isNeedProcess = true;
        for (Long nodeId : parentIds) {
            List<JobTaskBatch> jobTaskBatchList = currentWorkflowNodeMap.get(nodeId);
            if (CollUtil.isEmpty(jobTaskBatchList)) {
                // 递归查询有执行过的任务批次
                isNeedProcess = isNeedProcess || checkLeafCompleted(graph, currentWorkflowNodeMap, graph.predecessors(nodeId));
                continue;
            }

            for (JobTaskBatch jobTaskBatch : jobTaskBatchList) {
                // 只要是无需处理的说明后面的子节点都不需要处理了，isNeedProcess为false
                if (WORKFLOW_SUCCESSOR_SKIP_EXECUTION.contains(jobTaskBatch.getOperationReason())) {
                    isNeedProcess = false;
                    continue;
                }

                isNeedProcess = true;
            }

        }

        return isNeedProcess;
    }

    public boolean complete(Long workflowTaskBatchId) {
        return complete(workflowTaskBatchId, null);
    }

    public boolean complete(Long workflowTaskBatchId, WorkflowTaskBatch workflowTaskBatch) {
        workflowTaskBatch = Optional.ofNullable(workflowTaskBatch)
                .orElseGet(() -> workflowTaskBatchMapper.selectById(workflowTaskBatchId));
        Assert.notNull(workflowTaskBatch, () -> new SnailJobServerException("任务不存在"));

        String flowInfo = workflowTaskBatch.getFlowInfo();
        MutableGraph<Long> graph = MutableGraphCache.getOrDefault(workflowTaskBatchId, flowInfo);

        // 说明没有后继节点了, 此时需要判断整个DAG是否全部执行完成
        List<JobTaskBatch> jobTaskBatches = jobTaskBatchMapper.selectList(new LambdaQueryWrapper<JobTaskBatch>()
                .eq(JobTaskBatch::getWorkflowTaskBatchId, workflowTaskBatch.getId())
                .in(JobTaskBatch::getWorkflowNodeId, graph.nodes())
        );

        if (CollUtil.isEmpty(jobTaskBatches)) {
            return false;
        }

        if (jobTaskBatches.stream().anyMatch(
                jobTaskBatch -> JobTaskBatchStatusEnum.NOT_COMPLETE.contains(jobTaskBatch.getTaskBatchStatus()))) {
            return false;
        }

        Map<Long, List<JobTaskBatch>> currentWorkflowNodeMap = StreamUtils.groupByKey(jobTaskBatches,
                JobTaskBatch::getWorkflowNodeId);

        // 判定最后的工作流批次状态
        int taskStatus = JobTaskBatchStatusEnum.SUCCESS.getStatus();
        int operationReason = JobOperationReasonEnum.NONE.getReason();

        // 判定所有的叶子节点是否完成
        List<Long> leaves = MutableGraphCache.getLeaves(workflowTaskBatchId, flowInfo);
        for (Long leaf : leaves) {
            List<JobTaskBatch> jobTaskBatchList = currentWorkflowNodeMap.getOrDefault(leaf, Lists.newArrayList());
            if (CollUtil.isEmpty(jobTaskBatchList)) {
                boolean isNeedProcess = checkLeafCompleted(graph, currentWorkflowNodeMap, graph.predecessors(leaf));
                // 说明当前叶子节点需要处理，但是未处理返回false
                if (isNeedProcess) {
                    return false;
                }
            }

            boolean isMatchSuccess = jobTaskBatchList.stream()
                    .anyMatch(jobTaskBatch -> JobTaskStatusEnum.SUCCESS.getStatus() == jobTaskBatch.getTaskBatchStatus());
            if (!isMatchSuccess) {
                // 判定叶子节点的状态
                for (JobTaskBatch jobTaskBatch : jobTaskBatchList) {
                    if (jobTaskBatch.getTaskBatchStatus() == JobTaskBatchStatusEnum.SUCCESS.getStatus()) {
                        break;
                    } else if (JobTaskBatchStatusEnum.NOT_SUCCESS.contains(jobTaskBatch.getTaskBatchStatus())) {
                        // 只要叶子节点不是无需处理的都是失败
                        if (JobOperationReasonEnum.WORKFLOW_NODE_NO_REQUIRED.getReason() != jobTaskBatch.getOperationReason()
                                && JobOperationReasonEnum.WORKFLOW_NODE_CLOSED_SKIP_EXECUTION.getReason() != jobTaskBatch.getOperationReason()) {
                            taskStatus = JobTaskBatchStatusEnum.FAIL.getStatus();
                            SpringContext.getContext().publishEvent(new WorkflowTaskFailAlarmEvent(workflowTaskBatchId));
                        }
                    }
                }
            }
        }

        handlerTaskBatch(workflowTaskBatchId, taskStatus, operationReason);

        return true;

    }

    private void handlerTaskBatch(Long workflowTaskBatchId, int taskStatus, int operationReason) {

        WorkflowTaskBatch jobTaskBatch = new WorkflowTaskBatch();
        jobTaskBatch.setId(workflowTaskBatchId);
        jobTaskBatch.setTaskBatchStatus(taskStatus);
        jobTaskBatch.setOperationReason(operationReason);
        workflowTaskBatchMapper.updateById(jobTaskBatch);
    }

    public void stop(Long workflowTaskBatchId, Integer operationReason) {
        if (Objects.isNull(operationReason)
                || operationReason == JobOperationReasonEnum.NONE.getReason()) {
            operationReason = JobOperationReasonEnum.JOB_OVERLAY.getReason();
        }

        WorkflowTaskBatch workflowTaskBatch = new WorkflowTaskBatch();
        workflowTaskBatch.setTaskBatchStatus(JobTaskBatchStatusEnum.STOP.getStatus());
        workflowTaskBatch.setOperationReason(operationReason);
        workflowTaskBatch.setId(workflowTaskBatchId);
        // 先停止执行中的批次
        Assert.isTrue(1 == workflowTaskBatchMapper.updateById(workflowTaskBatch),
                () -> new SnailJobServerException("停止工作流批次失败. id:[{}]",
                        workflowTaskBatchId));
        SpringContext.getContext().publishEvent(new WorkflowTaskFailAlarmEvent(workflowTaskBatchId));

        // 关闭已经触发的任务
        List<JobTaskBatch> jobTaskBatches = jobTaskBatchMapper.selectList(new LambdaQueryWrapper<JobTaskBatch>()
                .in(JobTaskBatch::getTaskBatchStatus, NOT_COMPLETE)
                .eq(JobTaskBatch::getWorkflowTaskBatchId, workflowTaskBatchId));

        if (CollUtil.isEmpty(jobTaskBatches)) {
            return;
        }

        List<Job> jobs = jobMapper.selectBatchIds(StreamUtils.toSet(jobTaskBatches, JobTaskBatch::getJobId));

        Map<Long, Job> jobMap = StreamUtils.toIdentityMap(jobs, Job::getId);
        for (final JobTaskBatch jobTaskBatch : jobTaskBatches) {

            Job job = jobMap.get(jobTaskBatch.getJobId());
            if (Objects.nonNull(job)) {
                // 停止任务
                JobTaskStopHandler instanceInterrupt = JobTaskStopFactory.getJobTaskStop(job.getTaskType());
                TaskStopJobContext stopJobContext = JobTaskConverter.INSTANCE.toStopJobContext(job);
                stopJobContext.setTaskBatchId(jobTaskBatch.getId());
                stopJobContext.setJobOperationReason(JobOperationReasonEnum.JOB_TASK_INTERRUPTED.getReason());
                stopJobContext.setNeedUpdateTaskStatus(Boolean.TRUE);
                stopJobContext.setForceStop(Boolean.TRUE);
                instanceInterrupt.stop(stopJobContext);
            }

        }
    }

    public void checkWorkflowExecutor(Long workflowTaskBatchId, WorkflowTaskBatch workflowTaskBatch) throws IOException {
        workflowTaskBatch = Optional.ofNullable(workflowTaskBatch)
                .orElseGet(() -> workflowTaskBatchMapper.selectById(workflowTaskBatchId));
        Assert.notNull(workflowTaskBatch, () -> new SnailJobServerException("任务不存在"));
        String flowInfo = workflowTaskBatch.getFlowInfo();
        MutableGraph<Long> graph = MutableGraphCache.getOrDefault(workflowTaskBatchId, flowInfo);
        Set<Long> successors = graph.successors(SystemConstants.ROOT);
        if (CollUtil.isEmpty(successors)) {
            return;
        }

        // 说明没有后继节点了, 此时需要判断整个DAG是否全部执行完成
        List<JobTaskBatch> jobTaskBatches = jobTaskBatchMapper.selectList(new LambdaQueryWrapper<JobTaskBatch>()
                .eq(JobTaskBatch::getWorkflowTaskBatchId, workflowTaskBatchId)
                .in(JobTaskBatch::getWorkflowNodeId, graph.nodes()).orderByDesc(JobTaskBatch::getId)
        );

        Map<Long, JobTaskBatch> jobTaskBatchMap = StreamUtils.toIdentityMap(jobTaskBatches, JobTaskBatch::getWorkflowNodeId);

        checkWorkflowExecutor(SystemConstants.ROOT, workflowTaskBatchId, graph, jobTaskBatchMap);
    }

    private void checkWorkflowExecutor(Long parentId, Long workflowTaskBatchId, MutableGraph<Long> graph, Map<Long, JobTaskBatch> jobTaskBatchMap) {

        // 判定条件节点是否已经执行完成
        JobTaskBatch parentJobTaskBatch = jobTaskBatchMap.get(parentId);
        if (Objects.nonNull(parentJobTaskBatch) &&
                WORKFLOW_SUCCESSOR_SKIP_EXECUTION.contains(parentJobTaskBatch.getOperationReason())) {
            return;
        }

        Set<Long> successors = graph.successors(parentId);
        if (CollUtil.isEmpty(successors)) {
            return;
        }

        for (Long successor : successors) {
            JobTaskBatch jobTaskBatch = jobTaskBatchMap.get(successor);
            if (Objects.isNull(jobTaskBatch)) {
                // 重新尝试执行, 重新生成任务批次
                WorkflowNodeTaskExecuteDTO taskExecuteDTO = new WorkflowNodeTaskExecuteDTO();
                taskExecuteDTO.setWorkflowTaskBatchId(workflowTaskBatchId);
                taskExecuteDTO.setTaskExecutorScene(JobTaskExecutorSceneEnum.AUTO_WORKFLOW.getType());
                taskExecuteDTO.setParentId(parentId);
                if (Objects.nonNull(parentJobTaskBatch)) {
                    taskExecuteDTO.setTaskBatchId(parentJobTaskBatch.getId());
                }
                openNextNode(taskExecuteDTO);
                break;
            }

            if (NOT_COMPLETE.contains(jobTaskBatch.getTaskBatchStatus())) {
                // 生成任务批次
                Job job = jobMapper.selectById(jobTaskBatch.getJobId());
                JobTaskPrepareDTO jobTaskPrepare = JobTaskConverter.INSTANCE.toJobTaskPrepare(job);
                jobTaskPrepare.setTaskExecutorScene(JobTaskExecutorSceneEnum.AUTO_WORKFLOW.getType());
                jobTaskPrepare.setNextTriggerAt(DateUtils.toNowMilli() + DateUtils.toNowMilli() % 1000);
                jobTaskPrepare.setWorkflowTaskBatchId(workflowTaskBatchId);
                jobTaskPrepare.setWorkflowNodeId(successor);
                jobTaskPrepare.setParentWorkflowNodeId(parentId);
                // 执行预处理阶段
                ActorRef actorRef = ActorGenerator.jobTaskPrepareActor();
                actorRef.tell(jobTaskPrepare, actorRef);
                break;
            }

            // 已经是终态的需要递归遍历后继节点是否正常执行
            checkWorkflowExecutor(successor, workflowTaskBatchId, graph, jobTaskBatchMap);
        }
    }

    public void openNextNode(WorkflowNodeTaskExecuteDTO taskExecuteDTO) {
        if (Objects.isNull(taskExecuteDTO.getParentId()) || Objects.isNull(taskExecuteDTO.getWorkflowTaskBatchId())) {
            return;
        }

        // 若是工作流则开启下一个任务
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    tellWorkflowTaskExecutor(taskExecuteDTO);
                }
            });
        } else {
            tellWorkflowTaskExecutor(taskExecuteDTO);
        }
    }

    private void tellWorkflowTaskExecutor(WorkflowNodeTaskExecuteDTO taskExecuteDTO) {
        try {
            ActorRef actorRef = ActorGenerator.workflowTaskExecutorActor();
            actorRef.tell(taskExecuteDTO, actorRef);
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("任务调度执行失败", e);
        }
    }
}

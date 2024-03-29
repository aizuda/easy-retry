package com.aizuda.easy.retry.server.job.task.dto;

import lombok.Data;

/**
 * @author: www.byteblogs.com
 * @date : 2023-09-26 15:39
 */
@Data
public class TaskExecuteDTO {

    private Long jobId;
    private Long taskBatchId;
    /**
     * 工作流任务批次id
     */
    private Long workflowTaskBatchId;

    private Long workflowNodeId;
    /**
     * 执行策略 1、auto 2、manual 3、workflow
     */
    private Integer taskExecutorScene;

}

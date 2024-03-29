package com.aizuda.easy.retry.server.job.task.support;

import com.aizuda.easy.retry.server.job.task.dto.WorkflowTaskPrepareDTO;

/**
 * @author www.byteblogs.com
 * @date 2023-10-22 09:34:00
 * @since 2.6.0
 */
public interface WorkflowPrePareHandler {

    boolean matches(Integer status);

    void handler(WorkflowTaskPrepareDTO workflowTaskPrepareDTO);
}

package com.aizuda.easy.retry.server.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaowoniu
 * @date 2023-12-03 11:05:19
 * @since 2.5.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobAlarmInfo extends AlarmInfo {

    private Long id;

    /**
     * 命名空间
     */
    private String namespaceId;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 名称
     */
    private String jobName;

    /**
     * 任务信息id
     */
    private Long jobId;
    /**
     * 执行器名称
     */
    private String executorInfo;


    private String argsStr;

}

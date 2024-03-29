package com.aizuda.easy.retry.server.retry.task.dto;

import com.aizuda.easy.retry.server.common.dto.PartitionTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author xiaowoniu
 * @date 2023-10-25 22:23:24
 * @since 2.5.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NotifyConfigPartitionTask extends PartitionTask {


    private String namespaceId;

    private String groupName;

    private String sceneName;

    private Integer notifyStatus;

    private Integer notifyType;

    private String notifyAttribute;

    private Integer notifyThreshold;

    private Integer notifyScene;

    private Integer rateLimiterStatus;

    private Integer rateLimiterThreshold;

}

package com.aizuda.easy.retry.server.retry.task.support.dispatch.task;

import com.aizuda.easy.retry.server.common.enums.SyetemTaskTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author www.byteblogs.com
 * @date 2023-09-23 08:49:21
 * @since 2.4.0
 */
@AllArgsConstructor
@Getter
public enum TaskExecutorSceneEnum {
    AUTO_RETRY(1, SyetemTaskTypeEnum.RETRY),
    MANUAL_RETRY(2,  SyetemTaskTypeEnum.RETRY),
    AUTO_CALLBACK(3, SyetemTaskTypeEnum.CALLBACK),
    MANUAL_CALLBACK(4, SyetemTaskTypeEnum.CALLBACK);

    private final int scene;
    private final SyetemTaskTypeEnum taskType;


}

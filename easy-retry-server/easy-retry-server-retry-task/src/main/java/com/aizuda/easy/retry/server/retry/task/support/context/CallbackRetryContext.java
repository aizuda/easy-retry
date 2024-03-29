package com.aizuda.easy.retry.server.retry.task.support.context;

import com.aizuda.easy.retry.server.common.dto.RegisterNodeInfo;
import com.aizuda.easy.retry.server.retry.task.support.RetryContext;
import com.aizuda.easy.retry.server.common.WaitStrategy;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetryTask;
import com.aizuda.easy.retry.template.datasource.persistence.po.SceneConfig;
import lombok.Data;

import java.util.Objects;
import java.util.Set;

/**
 * @author www.byteblogs.com
 * @date 2023-06-04
 * @since 2.0
 */
@Data
public class CallbackRetryContext<V> implements RetryContext<V> {

    /**
     * 通知客户端回调结果
     */
    private V callResult;

    /**
     * 异常信息
     */
    private Exception exception;

    /**
     * 等待策略
     */
    private WaitStrategy waitStrategy;

    /**
     * 当前重试数据
     */
    private RetryTask retryTask;

    /**
     * 目前处理关闭的场景
     */
    private Set<String> sceneBlacklist;

    /**
     * 需要调度的节点
     */
    private RegisterNodeInfo serverNode;

    /**
     * 场景配置
     */
    private SceneConfig sceneConfig;

    @Override
    public boolean hasException() {
        return Objects.nonNull(exception);
    }

    @Override
    public SceneConfig sceneConfig() {
        return sceneConfig;
    }

}

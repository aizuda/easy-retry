package com.aizuda.easy.retry.server.retry.task.support.dispatch.task;

import akka.actor.ActorRef;
import com.aizuda.easy.retry.client.model.DispatchRetryResultDTO;
import com.aizuda.easy.retry.common.core.model.Result;
import com.aizuda.easy.retry.server.common.akka.ActorGenerator;
import com.aizuda.easy.retry.server.common.enums.TaskTypeEnum;
import com.aizuda.easy.retry.server.retry.task.support.RetryContext;
import com.aizuda.easy.retry.server.retry.task.support.WaitStrategy;
import com.aizuda.easy.retry.server.retry.task.support.context.MaxAttemptsPersistenceRetryContext;
import com.aizuda.easy.retry.server.retry.task.support.retry.RetryBuilder;
import com.aizuda.easy.retry.server.retry.task.support.retry.RetryExecutor;
import com.aizuda.easy.retry.server.retry.task.support.strategy.FilterStrategies;
import com.aizuda.easy.retry.server.retry.task.support.strategy.StopStrategies;
import com.aizuda.easy.retry.server.retry.task.support.strategy.WaitStrategies;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetryTask;
import com.aizuda.easy.retry.template.datasource.persistence.po.SceneConfig;
import org.springframework.stereotype.Component;

/**
 * 重试任务执行器
 *
 * @author www.byteblogs.com
 * @date 2023-09-23 08:03:07
 * @since 2.4.0
 */
@Component
public class RetryTaskActuator extends AbstractTaskActuator {

    @Override
    protected RetryContext<Result<DispatchRetryResultDTO>> builderRetryContext(final String groupName,
                                                                               final RetryTask retryTask) {
        MaxAttemptsPersistenceRetryContext<Result<DispatchRetryResultDTO>> retryContext = new MaxAttemptsPersistenceRetryContext<>();
        retryContext.setRetryTask(retryTask);
        retryContext.setSceneBlacklist(accessTemplate.getSceneConfigAccess().getBlacklist(groupName));
        retryContext.setServerNode(clientNodeAllocateHandler.getServerNode(retryTask.getGroupName()));
        return retryContext;
    }

    @Override
    protected RetryExecutor<Result<DispatchRetryResultDTO>> builderResultRetryExecutor(RetryContext retryContext) {

        RetryTask retryTask = retryContext.getRetryTask();
        return RetryBuilder.<Result<DispatchRetryResultDTO>>newBuilder()
                .withStopStrategy(StopStrategies.stopException())
                .withStopStrategy(StopStrategies.stopResultStatusCode())
                .withWaitStrategy(getWaitWaitStrategy(retryTask.getGroupName(), retryTask.getSceneName()))
                .withFilterStrategy(FilterStrategies.triggerAtFilter())
                .withFilterStrategy(FilterStrategies.bitSetIdempotentFilter(idempotentStrategy))
                .withFilterStrategy(FilterStrategies.sceneBlackFilter())
                .withFilterStrategy(FilterStrategies.checkAliveClientPodFilter())
                .withFilterStrategy(FilterStrategies.rebalanceFilterStrategies())
                .withFilterStrategy(FilterStrategies.rateLimiterFilter())
                .withRetryContext(retryContext)
                .build();
    }

    @Override
    public TaskActuatorSceneEnum getTaskType() {
        return TaskActuatorSceneEnum.AUTO_RETRY;
    }

    private WaitStrategy getWaitWaitStrategy(String groupName, String sceneName) {

        SceneConfig sceneConfig = accessTemplate.getSceneConfigAccess().getSceneConfigByGroupNameAndSceneName(groupName, sceneName);
        Integer backOff = sceneConfig.getBackOff();

        return WaitStrategies.WaitStrategyEnum.getWaitStrategy(backOff);
    }

    @Override
    protected ActorRef getActorRef() {
        return ActorGenerator.execUnitActor();
    }

}
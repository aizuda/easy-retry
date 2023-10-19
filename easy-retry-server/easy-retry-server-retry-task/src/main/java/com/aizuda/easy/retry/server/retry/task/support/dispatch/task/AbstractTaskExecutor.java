package com.aizuda.easy.retry.server.retry.task.support.dispatch.task;

import akka.actor.ActorRef;
import cn.hutool.core.lang.Pair;
import com.aizuda.easy.retry.server.common.config.SystemProperties;
import com.aizuda.easy.retry.server.common.handler.ClientNodeAllocateHandler;
import com.aizuda.easy.retry.server.common.IdempotentStrategy;
import com.aizuda.easy.retry.server.retry.task.support.RetryContext;
import com.aizuda.easy.retry.server.retry.task.support.retry.RetryExecutor;
import com.aizuda.easy.retry.template.datasource.access.AccessTemplate;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetryTask;
import com.aizuda.easy.retry.template.datasource.persistence.po.SceneConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 *
 * @author www.byteblogs.com
 * @date 2023-09-23 08:02:17
 * @since 2.4.0
 */
@Slf4j
public abstract class AbstractTaskExecutor implements TaskExecutor, InitializingBean {

    @Autowired
    @Qualifier("bitSetIdempotentStrategyHandler")
    protected IdempotentStrategy<String, Integer> idempotentStrategy;
    @Autowired
    protected SystemProperties systemProperties;
    @Autowired
    protected AccessTemplate accessTemplate;
    @Autowired
    protected ClientNodeAllocateHandler clientNodeAllocateHandler;

    @Override
    public void actuator(RetryTask retryTask) {
        // 重试次数累加
        retryCountIncrement(retryTask);

        SceneConfig sceneConfig = accessTemplate.getSceneConfigAccess().getSceneConfigByGroupNameAndSceneName(retryTask.getGroupName(), retryTask.getSceneName());

        RetryContext retryContext = builderRetryContext(retryTask.getGroupName(), retryTask, sceneConfig);
        RetryExecutor executor = builderResultRetryExecutor(retryContext, sceneConfig);

        if (!preCheck(retryContext, executor)) {
            return;
        }

        productExecUnitActor(executor);
    }

    protected boolean preCheck(RetryContext retryContext, RetryExecutor executor) {
        Pair<Boolean /*是否符合条件*/, StringBuilder/*描述信息*/> pair = executor.filter();
        if (!pair.getKey()) {
            log.warn("当前任务不满足执行条件. groupName:[{}] uniqueId:[{}], description:[{}]",
                    retryContext.getRetryTask().getGroupName(),
                    retryContext.getRetryTask().getUniqueId(), pair.getValue().toString());
            return false;
        }

        return true;
    }

    private void retryCountIncrement(RetryTask retryTask) {
        Integer retryCount = retryTask.getRetryCount();
        retryTask.setRetryCount(++retryCount);
    }

    protected void productExecUnitActor(RetryExecutor retryExecutor) {
        String groupIdHash = retryExecutor.getRetryContext().getRetryTask().getGroupName();
        Long retryId = retryExecutor.getRetryContext().getRetryTask().getId();
        idempotentStrategy.set(groupIdHash, retryId.intValue());

        ActorRef actorRef = getActorRef();
        actorRef.tell(retryExecutor, actorRef);
    }

    protected abstract RetryContext builderRetryContext(String groupName, RetryTask retryTask,
        final SceneConfig sceneConfig);

    protected abstract RetryExecutor builderResultRetryExecutor(RetryContext retryContext,
        final SceneConfig sceneConfig);

    protected abstract ActorRef getActorRef();

    @Override
    public void afterPropertiesSet() throws Exception {
        TaskActuatorFactory.register(this.getTaskType(), this);
    }
}

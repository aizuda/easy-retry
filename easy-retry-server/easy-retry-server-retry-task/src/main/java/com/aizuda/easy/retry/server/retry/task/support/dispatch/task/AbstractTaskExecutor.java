package com.aizuda.easy.retry.server.retry.task.support.dispatch.task;

import akka.actor.ActorRef;
import cn.hutool.core.lang.Pair;
import com.aizuda.easy.retry.server.common.akka.ActorGenerator;
import com.aizuda.easy.retry.server.common.config.SystemProperties;
import com.aizuda.easy.retry.server.common.handler.ClientNodeAllocateHandler;
import com.aizuda.easy.retry.server.common.IdempotentStrategy;
import com.aizuda.easy.retry.server.retry.task.support.RetryContext;
import com.aizuda.easy.retry.server.retry.task.support.dispatch.actor.log.RetryTaskLogDTO;
import com.aizuda.easy.retry.server.retry.task.support.retry.RetryExecutor;
import com.aizuda.easy.retry.template.datasource.access.AccessTemplate;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetryTask;
import com.aizuda.easy.retry.template.datasource.persistence.po.SceneConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;

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
    @Qualifier("retryIdempotentStrategyHandler")
    protected IdempotentStrategy<Pair<String/*groupName*/, String/*namespaceId*/>, Long> idempotentStrategy;
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

        SceneConfig sceneConfig = accessTemplate.getSceneConfigAccess().getSceneConfigByGroupNameAndSceneName(retryTask.getGroupName(), retryTask.getSceneName(),
            retryTask.getNamespaceId());

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
            RetryTask retryTask = retryContext.getRetryTask();
            log.warn("当前任务不满足执行条件. groupName:[{}] uniqueId:[{}], description:[{}]",
                retryTask.getGroupName(),
                retryTask.getUniqueId(), pair.getValue().toString());

            // 记录日志
            RetryTaskLogDTO retryTaskLog = new RetryTaskLogDTO();
            retryTaskLog.setGroupName(retryTask.getGroupName());
            retryTaskLog.setUniqueId(retryTask.getUniqueId());
            retryTaskLog.setRetryStatus(retryTask.getRetryStatus());
            retryTaskLog.setMessage(pair.getValue().toString());
            retryTaskLog.setTriggerTime(LocalDateTime.now());
            ActorRef actorRef = ActorGenerator.logActor();
            actorRef.tell(retryTaskLog, actorRef);

            return false;
        }

        return true;
    }

    private void retryCountIncrement(RetryTask retryTask) {
        Integer retryCount = retryTask.getRetryCount();
        retryTask.setRetryCount(++retryCount);
    }

    protected void productExecUnitActor(RetryExecutor retryExecutor) {
        RetryTask retryTask = retryExecutor.getRetryContext().getRetryTask();
        String groupName = retryTask.getGroupName();
        String namespaceId = retryTask.getNamespaceId();
        Long retryId = retryExecutor.getRetryContext().getRetryTask().getId();
        idempotentStrategy.set(Pair.of(groupName, namespaceId), retryId);

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

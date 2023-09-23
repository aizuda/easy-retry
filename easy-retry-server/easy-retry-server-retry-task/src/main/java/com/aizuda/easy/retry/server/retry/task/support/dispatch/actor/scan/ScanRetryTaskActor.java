package com.aizuda.easy.retry.server.retry.task.support.dispatch.actor.scan;

import akka.actor.ActorRef;
import com.aizuda.easy.retry.client.model.DispatchRetryResultDTO;
import com.aizuda.easy.retry.common.core.model.Result;
import com.aizuda.easy.retry.server.common.akka.ActorGenerator;
import com.aizuda.easy.retry.server.common.enums.TaskTypeEnum;
import com.aizuda.easy.retry.server.retry.task.support.RetryContext;
import com.aizuda.easy.retry.server.retry.task.support.WaitStrategy;
import com.aizuda.easy.retry.server.retry.task.support.context.MaxAttemptsPersistenceRetryContext;
import com.aizuda.easy.retry.server.retry.task.support.dispatch.task.TaskActuatorSceneEnum;
import com.aizuda.easy.retry.server.retry.task.support.retry.RetryBuilder;
import com.aizuda.easy.retry.server.retry.task.support.retry.RetryExecutor;
import com.aizuda.easy.retry.server.retry.task.support.strategy.FilterStrategies;
import com.aizuda.easy.retry.server.retry.task.support.strategy.StopStrategies;
import com.aizuda.easy.retry.server.retry.task.support.strategy.WaitStrategies;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetryTask;
import com.aizuda.easy.retry.template.datasource.persistence.po.SceneConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author www.byteblogs.com
 * @date 2021-10-30
 * @since 2.0
 */
@Component(ActorGenerator.SCAN_RETRY_GROUP_ACTOR)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class ScanRetryTaskActor extends AbstractScanGroup {

    /**
     * 缓存待拉取数据的起点id
     * <p>
     * LAST_AT_MAP[key] = groupName  LAST_AT_MAP[value] = retry_task的id
     */
    private static final ConcurrentMap<String, Long> LAST_AT_MAP = new ConcurrentHashMap<>();

    @Override
    protected TaskActuatorSceneEnum taskActuatorScene() {
        return TaskActuatorSceneEnum.AUTO_RETRY;
    }

    @Override
    protected Long getLastId(final String groupName) {
        return LAST_AT_MAP.get(groupName);
    }

    @Override
    protected void putLastId(final String groupName, final Long lastId) {
        LAST_AT_MAP.put(groupName, lastId);
    }

}
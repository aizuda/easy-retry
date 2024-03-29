package com.aizuda.easy.retry.server.retry.task.support.dispatch.actor.scan;

import akka.actor.AbstractActor;
import cn.hutool.core.lang.Pair;
import com.aizuda.easy.retry.common.core.constant.SystemConstants;
import com.aizuda.easy.retry.common.core.enums.RetryStatusEnum;
import com.aizuda.easy.retry.common.log.EasyRetryLog;
import com.aizuda.easy.retry.server.common.config.SystemProperties;
import com.aizuda.easy.retry.server.common.dto.PartitionTask;
import com.aizuda.easy.retry.server.common.dto.ScanTask;
import com.aizuda.easy.retry.server.common.handler.ClientNodeAllocateHandler;
import com.aizuda.easy.retry.server.common.util.DateUtils;
import com.aizuda.easy.retry.server.common.util.PartitionTaskUtils;
import com.aizuda.easy.retry.server.retry.task.dto.RetryPartitionTask;
import com.aizuda.easy.retry.server.retry.task.support.RetryTaskConverter;
import com.aizuda.easy.retry.server.retry.task.support.dispatch.task.TaskExecutor;
import com.aizuda.easy.retry.server.retry.task.support.dispatch.task.TaskExecutorSceneEnum;
import com.aizuda.easy.retry.server.retry.task.support.timer.RetryTimerWheel;
import com.aizuda.easy.retry.template.datasource.access.AccessTemplate;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.RetryTaskMapper;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetryTask;
import com.aizuda.easy.retry.template.datasource.persistence.po.SceneConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 数据扫描模板类
 *
 * @author: www.byteblogs.com
 * @date : 2023-06-05 15:44
 * @since 1.5.0
 */
@Slf4j
public abstract class AbstractScanGroup extends AbstractActor {

    @Autowired
    protected SystemProperties systemProperties;
    @Autowired
    protected AccessTemplate accessTemplate;
    @Autowired
    protected ClientNodeAllocateHandler clientNodeAllocateHandler;
    @Autowired
    protected List<TaskExecutor> taskExecutors;
    @Autowired
    protected RetryTaskMapper retryTaskMapper;

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ScanTask.class, config -> {

            // 获取开始时间
            long startTime = System.nanoTime();

            try {
                doScan(config);
            } catch (Exception e) {
                EasyRetryLog.LOCAL.error("Data scanner processing exception. [{}]", config, e);
            }

            // 获取结束时间
            long endTime = System.nanoTime();

            preCostTime().set((endTime - startTime) / 1_000_000);

        }).build();

    }

    protected void doScan(final ScanTask scanTask) {

        // 计算循环拉取的次数
        if (preCostTime().get() > 0) {
            long loopCount = Math.max((SystemConstants.SCHEDULE_PERIOD * 1000) / preCostTime().get(), 1);
            loopCount = Math.min(loopCount, systemProperties.getRetryMaxPullCount());
            prePullCount().set(loopCount);
        }

        String groupName = scanTask.getGroupName();
        String namespaceId = scanTask.getNamespaceId();
        Long lastId = Optional.ofNullable(getLastId(groupName)).orElse(0L);

        AtomicInteger count = new AtomicInteger(0);
        long total = PartitionTaskUtils.process(
                startId -> listAvailableTasks(groupName, namespaceId, startId, taskActuatorScene().getTaskType().getType()),
                partitionTasks1 -> processRetryPartitionTasks(partitionTasks1, scanTask), partitionTasks -> {
                    if (CollectionUtils.isEmpty(partitionTasks)) {
                        putLastId(scanTask.getGroupName(), 0L);
                        return Boolean.TRUE;
                    }

                    // 超过最大的拉取次数则中断
                    if (count.incrementAndGet() >= prePullCount().get()) {
                        putLastId(scanTask.getGroupName(), partitionTasks.get(partitionTasks.size() - 1).getId());
                        return Boolean.TRUE;
                    }

                    return false;
                }, lastId);

//        log.warn(this.getClass().getName() + " retry scan end. groupName:[{}] startId:[{}] preCostTime:[{}] total:[{}] realPullCount:[{}]",
//                groupName, lastId, preCostTime().get(), total, count.get());

    }

    private void processRetryPartitionTasks(List<? extends PartitionTask> partitionTasks, final ScanTask scanTask) {

        // 批次查询场景
        Map<String, SceneConfig> sceneConfigMap = getSceneConfigMap(partitionTasks, scanTask);

        List<RetryTask> waitUpdateRetryTasks = new ArrayList<>();
        for (PartitionTask task : partitionTasks) {
            RetryPartitionTask retryPartitionTask = (RetryPartitionTask) task;
            SceneConfig sceneConfig = sceneConfigMap.get(retryPartitionTask.getSceneName());
            if (Objects.isNull(sceneConfig)) {
                continue;
            }

            RetryTask retryTask = processRetryTask(retryPartitionTask, sceneConfig);
            waitUpdateRetryTasks.add(retryTask);
        }

        // 批量更新
        retryTaskMapper.updateBatchNextTriggerAtById(scanTask.getGroupPartition(), waitUpdateRetryTasks);

        long nowMilli = DateUtils.toNowMilli();
        for (PartitionTask partitionTask : partitionTasks) {
            RetryPartitionTask retryPartitionTask = (RetryPartitionTask) partitionTask;
            long delay = DateUtils.toEpochMilli(retryPartitionTask.getNextTriggerAt()) - nowMilli - nowMilli % 100;
            RetryTimerWheel.register(
                    Pair.of(retryPartitionTask.getGroupName(), retryPartitionTask.getNamespaceId()),
                    retryPartitionTask.getUniqueId(),
                    timerTask(retryPartitionTask),
                    delay,
                    TimeUnit.MILLISECONDS);
        }

    }

    private Map<String, SceneConfig> getSceneConfigMap(final List<? extends PartitionTask> partitionTasks, ScanTask scanTask) {
        Set<String> sceneNameSet = partitionTasks.stream()
                .map(partitionTask -> ((RetryPartitionTask) partitionTask).getSceneName()).collect(Collectors.toSet());
        List<SceneConfig> sceneConfigs = accessTemplate.getSceneConfigAccess()
                .list(new LambdaQueryWrapper<SceneConfig>()
                        .select(SceneConfig::getBackOff, SceneConfig::getTriggerInterval, SceneConfig::getSceneName)
                        .eq(SceneConfig::getNamespaceId, scanTask.getNamespaceId())
                        .eq(SceneConfig::getGroupName, scanTask.getGroupName())
                        .in(SceneConfig::getSceneName, sceneNameSet));
        return sceneConfigs.stream()
                .collect(Collectors.toMap(SceneConfig::getSceneName, i -> i));
    }

    private RetryTask processRetryTask(RetryPartitionTask partitionTask, SceneConfig sceneConfig) {
        RetryTask retryTask = new RetryTask();
        retryTask.setNextTriggerAt(calculateNextTriggerTime(partitionTask, sceneConfig));
        retryTask.setId(partitionTask.getId());
        return retryTask;
    }

    protected abstract TaskExecutorSceneEnum taskActuatorScene();

    protected abstract Long getLastId(String groupName);

    protected abstract void putLastId(String groupName, Long lastId);

    protected abstract LocalDateTime calculateNextTriggerTime(RetryPartitionTask partitionTask,
                                                              final SceneConfig sceneConfig);

    protected abstract TimerTask timerTask(RetryPartitionTask partitionTask);

    protected abstract AtomicLong preCostTime();

    protected abstract AtomicLong prePullCount();

    public List<RetryPartitionTask> listAvailableTasks(String groupName, String namespaceId, Long lastId, Integer taskType) {
        List<RetryTask> retryTasks = accessTemplate.getRetryTaskAccess()
                .listPage(groupName, namespaceId, new PageDTO<>(0, systemProperties.getRetryPullPageSize()),
                        new LambdaQueryWrapper<RetryTask>()
                                .select(RetryTask::getId, RetryTask::getNextTriggerAt, RetryTask::getUniqueId,
                                        RetryTask::getGroupName, RetryTask::getRetryCount, RetryTask::getSceneName,
                                        RetryTask::getNamespaceId)
                                .eq(RetryTask::getRetryStatus, RetryStatusEnum.RUNNING.getStatus())
                                .eq(RetryTask::getGroupName, groupName)
                                .eq(RetryTask::getNamespaceId, namespaceId)
                                .eq(RetryTask::getTaskType, taskType)
                                .le(RetryTask::getNextTriggerAt, LocalDateTime.now().plusSeconds(SystemConstants.SCHEDULE_PERIOD))
                                .gt(RetryTask::getId, lastId)
                                .orderByAsc(RetryTask::getId))
                .getRecords();

        return RetryTaskConverter.INSTANCE.toRetryPartitionTasks(retryTasks);
    }

}

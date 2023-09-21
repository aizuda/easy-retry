package com.aizuda.easy.retry.server.retry.task.support.cache;

import akka.actor.ActorRef;
import com.aizuda.easy.retry.common.core.log.LogUtils;
import com.aizuda.easy.retry.server.common.Lifecycle;
import com.aizuda.easy.retry.server.common.enums.TaskTypeEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 缓存组扫描Actor
 *
 * @author www.byteblogs.com
 * @date 2021-10-30
 * @since 1.0.0
 */
@Component
@Data
@Slf4j
public class CacheBucketActor implements Lifecycle {

    private static Cache<Integer, ActorRef> CACHE;

    /**
     * 获取所有缓存
     *
     * @return 缓存对象
     */
    public static ActorRef get(Integer bucket) {
       return CACHE.getIfPresent(bucket);
    }

    /**
     * 获取所有缓存
     *
     * @return 缓存对象
     */
    public static void put(Integer bucket, ActorRef actorRef) {
         CACHE.put(bucket, actorRef);
    }

    @Override
    public void start() {
        LogUtils.info(log, "CacheGroupScanActor start");
        CACHE = CacheBuilder.newBuilder()
                // 设置并发级别为cpu核心数
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Override
    public void close() {
        LogUtils.info(log, "CacheGroupScanActor stop");
        CACHE.invalidateAll();
    }
}
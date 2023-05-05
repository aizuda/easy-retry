package com.aizuda.easy.retry.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 系统配置
 *
 * @author: www.byteblogs.com
 * @date : 2021-12-21 10:19
 */
@Configuration
@ConfigurationProperties(value = "easy-retry")
@Data
public class SystemProperties {

    /**
     * 拉取数据天数
     */
    private int lastDays = 30;

    /**
     * 重试每次拉取的条数
     */
    private int retryPullPageSize = 100;

    /**
     * netty 端口
     */
    private int nettyPort = 1788;

    /**
     * 分区数
     */
    private int totalPartition = 32;

    /**
     * 一个客户端每秒最多接收的重试数量指令
     */
    private int limiter = 10;

    /**
     * 号段模式下步长配置
     * 默认100
     */
    private int step = 100;

}

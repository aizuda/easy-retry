package com.aizuda.easy.retry.client.common.init;

import com.aizuda.easy.retry.client.common.Lifecycle;
import com.aizuda.easy.retry.client.common.event.EasyRetryStartedEvent;
import com.aizuda.easy.retry.client.common.event.EasyRetryStartingEvent;
import com.aizuda.easy.retry.common.core.constant.SystemConstants;
import com.aizuda.easy.retry.common.core.context.SpringContext;
import com.aizuda.easy.retry.common.core.util.EasyRetryVersion;
import com.aizuda.easy.retry.common.log.EasyRetryLog;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统启动监听器
 *
 * @author: www.byteblogs.com
 * @date : 2021-11-19 19:00
 */
@Component
public class EasyRetryStartListener implements ApplicationRunner {

    @Autowired
    private List<Lifecycle> lifecycleList;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(MessageFormatter.format(SystemConstants.LOGO, EasyRetryVersion.getVersion()).getMessage());

        EasyRetryLog.LOCAL.info("Easy-Retry client is preparing to start... v{}", EasyRetryVersion.getVersion());
        SpringContext.CONTEXT.publishEvent(new EasyRetryStartingEvent());
        lifecycleList.forEach(Lifecycle::start);
        SpringContext.CONTEXT.publishEvent(new EasyRetryStartedEvent());
        EasyRetryLog.LOCAL.info("Easy-Retry client started successfully v{}", EasyRetryVersion.getVersion());
    }

}

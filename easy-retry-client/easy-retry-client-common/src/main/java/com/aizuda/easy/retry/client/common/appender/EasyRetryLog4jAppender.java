package com.aizuda.easy.retry.client.common.appender;

import com.aizuda.easy.retry.client.common.report.AsyncReportLog;
import com.aizuda.easy.retry.client.common.util.ThreadLocalLogUtil;
import com.aizuda.easy.retry.common.log.dto.LogContentDTO;
import com.aizuda.easy.retry.common.log.constant.LogFieldConstants;
import com.aizuda.easy.retry.common.core.context.SpringContext;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Objects;

/**
 * @author wodeyangzipingpingwuqi
 * @date 2023-12-27
 * @since 2.6.0
 */
public class EasyRetryLog4jAppender extends AppenderSkeleton {

    @Override
    public void activateOptions() {
        super.activateOptions();
    }

    @Override
    protected void append(LoggingEvent event) {

        // Not job context
        if (Objects.isNull(ThreadLocalLogUtil.getContext()) || Objects.isNull(MDC.get(LogFieldConstants.MDC_REMOTE))) {
            return;
        }

        MDC.remove(LogFieldConstants.MDC_REMOTE);
        LogContentDTO logContentDTO = new LogContentDTO();
        logContentDTO.addTimeStamp(event.getTimeStamp());
        logContentDTO.addLevelField(event.getLevel().toString());
        logContentDTO.addThreadField(event.getThreadName());
        logContentDTO.addMessageField(event.getMessage().toString());
        logContentDTO.addLocationField(event.getLocationInformation().fullInfo);
        logContentDTO.addThrowableField(getThrowableField(event));

        // slidingWindow syncReportLog
        SpringContext.getBeanByType(AsyncReportLog.class).syncReportLog(logContentDTO);
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    private String getThrowableField(LoggingEvent event) {
        String throwable = getThrowableStr(event);
        if (throwable != null) {
            return throwable;
        }
        return null;
    }

    private String getThrowableStr(LoggingEvent event) {
        ThrowableInformation throwable = event.getThrowableInformation();
        if (throwable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : throwable.getThrowableStrRep()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(s);
        }
        return sb.toString();
    }
}

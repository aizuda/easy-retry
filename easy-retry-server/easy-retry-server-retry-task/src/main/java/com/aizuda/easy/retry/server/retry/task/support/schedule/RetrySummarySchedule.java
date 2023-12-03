package com.aizuda.easy.retry.server.retry.task.support.schedule;

import com.aizuda.easy.retry.common.core.log.LogUtils;
import com.aizuda.easy.retry.server.common.Lifecycle;
import com.aizuda.easy.retry.server.common.schedule.AbstractSchedule;
import com.aizuda.easy.retry.template.datasource.persistence.dataobject.DashboardRetryResponseDO;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.RetrySummaryMapper;
import com.aizuda.easy.retry.template.datasource.persistence.mapper.RetryTaskLogMapper;
import com.aizuda.easy.retry.template.datasource.persistence.po.RetrySummary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Retry Dashboard
 *
 * @author: wodeyangzipingpingwuqi
 * @date : 2023-11-28 13:46
 * @since 2.1.0
 */
@Component
@Slf4j
public class RetrySummarySchedule extends AbstractSchedule implements Lifecycle {

    @Autowired
    private RetryTaskLogMapper retryTaskLogMapper;
    @Autowired
    private RetrySummaryMapper retrySummaryMapper;

    @Override
    public String lockName() {
        return "retrySummaryDashboard";
    }

    @Override
    public String lockAtMost() {
        return "PT1M";
    }

    @Override
    public String lockAtLeast() {
        return "PT20S";
    }

    @Override
    protected void doExecute() {
        try {
            // 重试按日实时查询统计数据（00:00:00 - 23:59:59）
            LocalDateTime todayFrom = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayTo = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            List<DashboardRetryResponseDO> dashboardRetryResponseDOList = retryTaskLogMapper.retrySummaryRetryTaskLogList(todayFrom, todayTo);
            if (dashboardRetryResponseDOList == null || dashboardRetryResponseDOList.size() < 1) {
                return;
            }

            // insertOrUpdate
            List<RetrySummary> retrySummaryList = retrySummaryList(todayFrom, dashboardRetryResponseDOList);
            int total = retrySummaryMapper.updateBatchSceneNameById(retrySummaryList);
            if (total < 1) {
                retrySummaryMapper.insertBatchRetrySummary(retrySummaryList);
            }
            LogUtils.debug(log, "retry summary dashboard success todayFrom:[{}] todayTo:[{}] total:[{}]", todayFrom, todayTo, total);
        } catch (Exception e) {
            LogUtils.error(log, "retry summary dashboard log error", e);
        }
    }

    private List<RetrySummary> retrySummaryList(LocalDateTime triggerAt, List<DashboardRetryResponseDO> dashboardRetryResponseDOList) {
        List<RetrySummary> retrySummaryList = new ArrayList<>();
        for (DashboardRetryResponseDO dashboardRetryResponseDO : dashboardRetryResponseDOList) {
            RetrySummary retrySummary = new RetrySummary();
            retrySummary.setTriggerAt(triggerAt);
            retrySummary.setNamespaceId(dashboardRetryResponseDO.getNamespaceId());
            retrySummary.setGroupName(dashboardRetryResponseDO.getGroupName());
            retrySummary.setSceneName(dashboardRetryResponseDO.getSceneName());
            retrySummary.setRunningNum(dashboardRetryResponseDO.getRunningNum());
            retrySummary.setFinishNum(dashboardRetryResponseDO.getFinishNum());
            retrySummary.setMaxCountNum(dashboardRetryResponseDO.getMaxCountNum());
            retrySummary.setSuspendNum(dashboardRetryResponseDO.getSuspendNum());
            retrySummaryList.add(retrySummary);
        }
        return retrySummaryList;
    }

    @Override
    public void start() {
        taskScheduler.scheduleAtFixedRate(this::execute, Duration.parse("PT1M"));
    }

    @Override
    public void close() {

    }
}
package com.aizuda.easy.retry.client.core.strategy;

import com.aizuda.easy.retry.client.core.RetryExecutor;
import com.aizuda.easy.retry.client.core.RetryExecutorParameter;
import com.aizuda.easy.retry.client.core.event.EasyRetryListener;
import com.aizuda.easy.retry.client.core.intercepter.RetrySiteSnapshot;
import com.aizuda.easy.retry.client.core.Report;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategy;
import com.aizuda.easy.retry.client.core.executor.GuavaRetryExecutor;
import com.aizuda.easy.retry.client.core.retryer.RetryerInfo;
import com.aizuda.easy.retry.client.core.retryer.RetryerResultContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author: www.byteblogs.com
 * @date : 2022-03-04 14:40
 */
@Slf4j
public abstract class AbstractRetryStrategies implements RetryStrategy {

    @Autowired
    private List<EasyRetryListener> easyRetryListeners;

    @Autowired
    private List<Report> reports;

    @Override
    public RetryerResultContext openRetry(String sceneName, String executorClassName, Object[] params) {

        RetryerResultContext retryerResultContext = new RetryerResultContext();

        // 开始内存重试
        RetryExecutor<WaitStrategy, StopStrategy> retryExecutor =
                new GuavaRetryExecutor(sceneName, executorClassName);
        RetryerInfo retryerInfo = retryExecutor.getRetryerInfo();

        if (!preValidator(retryerInfo, retryerResultContext)) {
            return retryerResultContext;
        }

        RetrySiteSnapshot.setStatus(RetrySiteSnapshot.EnumStatus.RUNNING.getStatus());

        setStage();

        Retryer retryer = retryExecutor.build(getRetryExecutorParameter(retryerInfo));

        retryerResultContext.setRetryerInfo(retryerInfo);

        try {
            for (EasyRetryListener EasyRetryListener : easyRetryListeners) {
                EasyRetryListener.beforeRetry(sceneName, executorClassName, params);
            }

            Object result = retryExecutor.call(retryer, doGetCallable(retryExecutor, params), getRetryErrorConsumer(retryerResultContext, params), getRetrySuccessConsumer(retryerResultContext));
            retryerResultContext.setResult(result);

        } catch (Exception e) {
            log.error("重试执行非预期异常", e);
            retryerResultContext.setMessage("非预期异常" + e.getMessage());
            // 本地重试状态未失败 远程重试状态为成功
            unexpectedError(e, retryerResultContext);
        }

        return retryerResultContext;
    }

    protected abstract void setStage();

    protected Consumer<Object> getRetrySuccessConsumer(RetryerResultContext retryerResultContext) {
        return o -> {

            success(retryerResultContext);

            Object result = retryerResultContext.getResult();
            RetryerInfo retryerInfo = retryerResultContext.getRetryerInfo();

            for (EasyRetryListener EasyRetryListener : easyRetryListeners) {
                EasyRetryListener.successOnRetry(result, retryerInfo.getScene(), retryerInfo.getExecutorClassName());
            }

            doRetrySuccessConsumer(retryerResultContext).accept(retryerResultContext);
        };
    }


    protected abstract Consumer<Object> doRetrySuccessConsumer(RetryerResultContext context);

    private Consumer<Throwable> getRetryErrorConsumer(RetryerResultContext context, Object... params) {
        return throwable -> {
            context.setThrowable(throwable);
            context.setMessage(throwable.getMessage());

            error(context);

            RetryerInfo retryerInfo = context.getRetryerInfo();
            try {
                for (EasyRetryListener EasyRetryListener : easyRetryListeners) {
                    EasyRetryListener
                        .failureOnRetry(retryerInfo.getScene(), retryerInfo.getExecutorClassName(), throwable);
                }
            } catch (Exception e) {
                log.error("失败监听者模式 处理失败 ", e);
                throw e;
            } finally {
                RetrySiteSnapshot.setStatus(RetrySiteSnapshot.EnumStatus.COMPLETE.getStatus());
            }

            doGetRetryErrorConsumer(retryerInfo, params).accept(throwable);

        };
    }

    protected abstract void error(RetryerResultContext context);

    protected abstract boolean preValidator(RetryerInfo retryerInfo, RetryerResultContext resultContext);
    protected abstract void unexpectedError(Exception e, RetryerResultContext retryerResultContext);
    protected abstract void success(RetryerResultContext retryerResultContext);
    protected abstract Consumer<Throwable> doGetRetryErrorConsumer(RetryerInfo retryerInfo, Object[] params);
    protected abstract Callable doGetCallable(RetryExecutor<WaitStrategy, StopStrategy> retryExecutor,Object[] params);
    protected abstract RetryExecutorParameter<WaitStrategy, StopStrategy> getRetryExecutorParameter(RetryerInfo retryerInfo);
    /**
     * 上报数据
     *
     * @param retryerInfo 定义重试场景的信息
     * @param params 执行参数
     */
    protected boolean doReport(final RetryerInfo retryerInfo, final Object[] params) {

        for (Report report : reports) {
            if (report.supports(retryerInfo.isAsync())) {
                return report.report(retryerInfo.getScene(), retryerInfo.getExecutorClassName(), params);
            }
        }

        return Boolean.FALSE;
    }

}

package com.aizuda.easy.retry.common.log.level;

/**
 * DEBUG级别日志接口
 *
 * @author wodeyangzipingpingwuqi
 */
public interface DebugLog {
    /**
     * @return DEBUG 等级是否开启
     */
    boolean isDebugEnabled();

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param t 错误对象
     */
    void debug(Throwable t, Boolean remote);

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param format    消息模板
     * @param arguments 参数
     */
    void debug(String format, Boolean remote, Object... arguments);

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param t         错误对象
     * @param format    消息模板
     * @param arguments 参数
     */
    void debug(Throwable t, String format, Boolean remote, Object... arguments);

    /**
     * 打印 DEBUG 等级的日志
     *
     * @param fqcn      完全限定类名(Fully Qualified Class Name)，用于定位日志位置
     * @param t         错误对象
     * @param format    消息模板
     * @param arguments 参数
     */
    void debug(String fqcn, Throwable t, String format, Boolean remote, Object... arguments);
}

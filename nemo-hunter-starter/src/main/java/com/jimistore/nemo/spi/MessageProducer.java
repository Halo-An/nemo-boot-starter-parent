package com.jimistore.nemo.spi;

/**
 * 生产者
 *
 * @author hexin
 */
public interface MessageProducer {

    /**
     * 错误日志
     * @param cause
     */
    void logError(Throwable cause);

    /**
     * 错误日志
     * @param message
     * @param cause
     */
    void logError(String message, Throwable cause);

    /**
     * 事件日志
     * @param type
     * @param name
     */
    void logEvent(String type, String name);

    /**
     * 事件日志
     * @param type
     * @param name
     * @param status
     * @param nameValuePairs
     */
    void logEvent(String type, String name, String status, String nameValuePairs);

    /**
     * 创建transaction
     * @param type
     * @param name
     * @return
     */
    Transaction newTransaction(String type, String name);

    /**
     * 业务指标总和
     * @param name
     */
    void logMetricForCount(String name);

    /**
     * 业务指标总和
     * @param name
     * @param quantity
     */
    void logMetricForCount(String name, int quantity);

    /**
     * 业务指标平均值
     * @param name
     * @param durationInMillis
     */
    void logMetricForDuration(String name, long durationInMillis);
}

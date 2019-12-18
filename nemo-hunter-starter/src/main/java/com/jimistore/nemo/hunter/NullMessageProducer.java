package com.jimistore.nemo.hunter;

import com.jimistore.nemo.spi.MessageProducer;
import com.jimistore.nemo.spi.Transaction;

/**
 * 空实现
 * 未接入cat时不触发埋点
 *
 * @author hexin
 */
public class NullMessageProducer implements MessageProducer {

    private static final Transaction NULL_TRANSACTION = new NullTransaction();

    @Override
    public void logError(Throwable cause) {
    }

    @Override
    public void logError(String message, Throwable cause) {
    }

    @Override
    public void logEvent(String type, String name) {
    }

    @Override
    public void logEvent(String type, String name, String status, String nameValuePairs) {
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        return NULL_TRANSACTION;
    }

    @Override
    public void logMetricForCount(String name) {

    }

    @Override
    public void logMetricForCount(String name, int quantity) {

    }

    @Override
    public void logMetricForDuration(String name, long durationInMillis) {

    }
}

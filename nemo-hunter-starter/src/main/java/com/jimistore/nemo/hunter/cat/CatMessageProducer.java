package com.jimistore.nemo.hunter.cat;

import com.jimistore.nemo.spi.MessageProducer;
import com.jimistore.nemo.spi.Transaction;

import java.lang.reflect.Method;

/**
 * 自定义cat生产者
 *
 * @author hexin
 */
public class CatMessageProducer implements MessageProducer {

    private static Class CAT_CLASS;

    private static Method LOG_ERROR_WITH_CAUSE;
    private static Method LOG_ERROR_WITH_MESSAGE_AND_CAUSE;

    private static Method LOG_EVENT_WITH_TYPE_AND_NAME;
    private static Method LOG_EVENT_WITH_TYPE_AND_NAME_AND_STATUS_ANDNAMEVALUE;

    private static Method NEW_TRANSACTION_WITH_TYPE_AND_NAME;

    private static Method LOG_METRIC_FOR_COUNT;
    private static Method LOG_METRIC_FOR_COUNT_WITH_QUANTITY;
    private static Method LOG_METRIC_FOR_DURATION;

    static {
        try {
            CAT_CLASS = Class.forName(CatNames.CAT_CLASS);

            LOG_ERROR_WITH_CAUSE = CAT_CLASS.getMethod(CatNames.LOG_ERROR_METHOD, Throwable.class);
            LOG_ERROR_WITH_MESSAGE_AND_CAUSE = CAT_CLASS.getMethod(CatNames.LOG_ERROR_METHOD, String.class, Throwable.class);

            LOG_EVENT_WITH_TYPE_AND_NAME = CAT_CLASS.getMethod(CatNames.LOG_EVENT_METHOD, String.class, String.class);
            LOG_EVENT_WITH_TYPE_AND_NAME_AND_STATUS_ANDNAMEVALUE = CAT_CLASS.getMethod(CatNames.LOG_EVENT_METHOD,
                    String.class, String.class, String.class, String.class);

            NEW_TRANSACTION_WITH_TYPE_AND_NAME = CAT_CLASS.getMethod(CatNames.NEW_TRANSACTION_METHOD, String.class, String.class);
            CatTransaction.init();

            LOG_METRIC_FOR_COUNT = CAT_CLASS.getMethod(CatNames.LOG_METRIC_FOR_COUNT, String.class);
            LOG_METRIC_FOR_COUNT_WITH_QUANTITY = CAT_CLASS.getMethod(CatNames.LOG_METRIC_FOR_COUNT, String.class, int.class);
            LOG_METRIC_FOR_DURATION = CAT_CLASS.getMethod(CatNames.LOG_METRIC_FOR_DURATION, String.class, long.class);
        }catch (Throwable e){
            throw new IllegalStateException("init caMessageProducer fail", e);
        }
    }

    @Override
    public void logError(Throwable cause) {
        try {
            LOG_ERROR_WITH_CAUSE.invoke(null, cause);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void logError(String message, Throwable cause) {
        try {
            LOG_ERROR_WITH_MESSAGE_AND_CAUSE.invoke(null, message, cause);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void logEvent(String type, String name) {
        try {
            LOG_EVENT_WITH_TYPE_AND_NAME.invoke(null, type, name);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void logEvent(String type, String name, String status, String nameValuePairs) {
        try {
            LOG_EVENT_WITH_TYPE_AND_NAME_AND_STATUS_ANDNAMEVALUE.invoke(null,
                    type, name, status, nameValuePairs);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        try {
            return new CatTransaction(NEW_TRANSACTION_WITH_TYPE_AND_NAME.invoke(null, type, name));
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void logMetricForCount(String name) {
        try {
            LOG_METRIC_FOR_COUNT.invoke(null, name);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void logMetricForCount(String name, int quantity) {
        try {
            LOG_METRIC_FOR_COUNT_WITH_QUANTITY.invoke(null, name, quantity);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void logMetricForDuration(String name, long durationInMillis) {
        try {
            LOG_METRIC_FOR_DURATION.invoke(null, name, durationInMillis);
        }catch (Throwable e){
            throw new IllegalStateException(e);
        }
    }
}

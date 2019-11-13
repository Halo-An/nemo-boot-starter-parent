package com.jimistore.nemo.hunter;

import com.jimistore.nemo.spi.MessageProducer;
import com.jimistore.nemo.spi.MessageProducerManager;
import com.jimistore.nemo.spi.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监控入口
 *
 * @author hexin
 */
public abstract class Hunter {

    private static final Logger logger = LoggerFactory.getLogger(Hunter.class);

    private static final MessageProducerManager NULL_MESSAGE_PRODUCTOR_MANAGER =
            new NullMessageProducerManager();

    private static volatile MessageProducerManager producerManager;

    private static Object lock = new Object();

    static {
        getProducer();
    }

    private static MessageProducer getProducer(){
        try {
            if (producerManager == null){
                synchronized (lock){
                    if (producerManager == null){
                        producerManager = new DefaultMessageProducerManager();
                    }
                }
            }
        }catch (Throwable e){
            logger.error("init messageProducer fail", e);
            producerManager = NULL_MESSAGE_PRODUCTOR_MANAGER;
        }
        return producerManager.geProducer();
    }

    public static void logError(Throwable cause){
        try {
            getProducer().logError(cause);
        }catch (Throwable e){
            logger.warn("log error fail, cause: {}", cause, e);
        }
    }

    public static void logError(String message, Throwable cause){
        try {
            getProducer().logError(message, cause);
        }catch (Throwable e){
            logger.warn("log errorWithMessage fail, message: {}, cause: {}", message, cause, e);
        }
    }

    public static void logEvent(String type, String name){
        try {
            getProducer().logEvent(type, name);
        }catch (Throwable e){
            logger.warn("log event fail, type: {}, name: {}", type, name, e);
        }
    }

    public static void logEvent(String type, String name, String status, String nameValuePairs){
        try {
            getProducer().logEvent(type, name, status, nameValuePairs);
        }catch (Throwable e){
            logger.warn("log event with nameValuePairs fail, type: {}, name: {}, status: {}, nameValuePairs: {}",
                    type, name, status, nameValuePairs);
        }
    }

    public static Transaction newTransaction(String type, String name){
        try {
            return getProducer().newTransaction(type, name);
        }catch (Throwable e){
            logger.warn("newTransaction fail, type: {}, name: {}", type, name);
            return NULL_MESSAGE_PRODUCTOR_MANAGER.geProducer().newTransaction(type, name);
        }
    }

    public static void logMetricForCount(String name) {
        try {
            getProducer().logMetricForCount(name);
        }catch (Throwable e){
            logger.warn("log metric fail, name : {}", name, e);
        }
    }

    public static void logMetricForCount(String name, int quantity) {
        try {
            getProducer().logMetricForCount(name, quantity);
        }catch (Throwable e){
            logger.warn("log metric fail, name : {}", name, e);
        }
    }

    public static void logMetricForDuration(String name, long durationInMillis) {
        try {
            getProducer().logMetricForDuration(name, durationInMillis);
        }catch (Throwable e){
            logger.warn("log metric for duration fail, name : {}", name, e);
        }
    }

}
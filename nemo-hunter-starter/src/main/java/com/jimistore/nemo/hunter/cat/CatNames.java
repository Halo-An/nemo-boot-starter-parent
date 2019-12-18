package com.jimistore.nemo.hunter.cat;

/**
 * cat类路径方法名常量
 *
 * @author hexin
 */
public interface CatNames {

    String CAT_CLASS = "com.dianping.cat.Cat";
    String LOG_ERROR_METHOD = "logError";
    String LOG_EVENT_METHOD = "logEvent";
    String NEW_TRANSACTION_METHOD = "newTransaction";
    String LOG_METRIC_FOR_COUNT = "logMetricForCount";
    String LOG_METRIC_FOR_DURATION = "logMetricForDuration";

    String CAT_TRANSACTION_CLASS = "com.dianping.cat.message.Transaction";
    String SET_STATUS_METHOD = "setStatus";
    String ADD_DATA_METHOD = "addData";
    String COMPLETE_METHOD = "complete";
}
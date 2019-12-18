package com.jimistore.nemo.spi;

/**
 * Transaction
 *
 * @author hexin
 */
public interface Transaction {

    String SUCCESS = "0";

    /**
     * 状态消息
     * @param status
     */
    void setStatus(String status);

    /**
     * 异常状态消息
     * @param e
     */
    void setStatus(Throwable e);

    /**
     * 添加数据
     * @param key
     * @param value
     */
    void addData(String key, Object value);

    /**
     * 完成
     */
    void complete();
}

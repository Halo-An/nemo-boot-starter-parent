package com.jimistore.nemo.spi;

/**
 * 生产者管理
 *
 * @author hexin
 */
public interface MessageProducerManager {

    /**
     * getProducter
     * @return
     */
    MessageProducer geProducer();
}

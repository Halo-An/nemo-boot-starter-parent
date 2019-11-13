package com.jimistore.nemo.hunter;

import com.jimistore.nemo.spi.MessageProducer;
import com.jimistore.nemo.spi.MessageProducerManager;

/**
 * 空实现
 * 未接入cat时不触发埋点
 *
 * @author hexin
 */
public class NullMessageProducerManager implements MessageProducerManager {

    private static final MessageProducer producer = new NullMessageProducer();

    @Override
    public MessageProducer geProducer() {
        return producer;
    }
}
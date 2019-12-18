package com.jimistore.nemo.hunter;

import com.jimistore.nemo.spi.MessageProducer;
import com.jimistore.nemo.spi.MessageProducerManager;
import com.jimistore.nemo.hunter.cat.CatMessageProducer;
import com.jimistore.nemo.hunter.cat.CatNames;
import com.jimistore.nemo.util.ClassLoaderUtil;

/**
 * 默认生产者
 *
 * @author hexin
 */
public class DefaultMessageProducerManager implements MessageProducerManager {

    private static MessageProducer messageProducer;

    public DefaultMessageProducerManager(){
        if (ClassLoaderUtil.isClassPresent(CatNames.CAT_CLASS)){
            messageProducer = new CatMessageProducer();
        }else {
            messageProducer = new NullMessageProducerManager().geProducer();
        }
    }

    @Override
    public MessageProducer geProducer() {
        return messageProducer;
    }

}
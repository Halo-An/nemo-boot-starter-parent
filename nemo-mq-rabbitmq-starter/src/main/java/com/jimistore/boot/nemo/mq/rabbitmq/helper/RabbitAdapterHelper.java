package com.jimistore.boot.nemo.mq.rabbitmq.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;

public class RabbitAdapterHelper implements BeanFactoryPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(RabbitAdapterHelper.class);

	private DefaultListableBeanFactory dlbf;

	private Map<IMQReceiver, CachingConnectionFactory> mQReceiverMap = new HashMap<IMQReceiver, CachingConnectionFactory>();

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.dlbf = (DefaultListableBeanFactory) beanFactory;
		this.initListener();
	}

	public void initListener(IMQReceiver mQReceiver, CachingConnectionFactory cachingConnectionFactory) {
		mQReceiverMap.put(mQReceiver, cachingConnectionFactory);
		this.initListener();
	}

	private void initListener() {

		if (this.dlbf == null) {
			return;
		}
		Iterator<Entry<IMQReceiver, CachingConnectionFactory>> it = mQReceiverMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<IMQReceiver, CachingConnectionFactory> entry = it.next();
			IMQReceiver mQReceiver = entry.getKey();
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
					.rootBeanDefinition(DefaultMessageListenerContainer.class)
					.addPropertyValue("connectionFactory", entry.getValue())
					.addPropertyValue("queueNames", mQReceiver.getmQName())
					.addPropertyValue("messageListener", new MessageListener() {

						@Override
						public void onMessage(Message message) {
							try {
								LOG.info(String.format("receive a message, mqname is [%s]", mQReceiver.getmQName()));
								mQReceiver.receive(new String(message.getBody()));
							} catch (Throwable e) {
								LOG.warn("handle massage throw a exception", e);
								if (e instanceof RuntimeException) {
									throw (RuntimeException) e;
								} else {
									throw new RuntimeException(e);
								}
							}
						}

					});

			LOG.info(String.format("add a message listener[%s]", mQReceiver.getmQName()));
			dlbf.registerBeanDefinition(String.format("rabbitmq-%s-clientProxy", mQReceiver.getmQName()),
					beanDefinitionBuilder.getBeanDefinition());

			it.remove();
		}

	}
}

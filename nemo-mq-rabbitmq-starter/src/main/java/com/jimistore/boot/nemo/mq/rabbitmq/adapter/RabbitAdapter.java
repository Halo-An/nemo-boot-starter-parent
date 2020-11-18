package com.jimistore.boot.nemo.mq.rabbitmq.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.adapter.MQMessage;
import com.jimistore.boot.nemo.mq.rabbitmq.helper.RabbitAdapterHelper;

public class RabbitAdapter implements IMQAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(RabbitAdapter.class);

	RabbitAdapterHelper rabbitAdapterHelper;

	RabbitProperties rabbitProperties;

	CachingConnectionFactory cachingConnectionFactory;

	RabbitTemplate rabbitTemplate;

	public RabbitAdapter setRabbitAdapterHelper(RabbitAdapterHelper rabbitAdapterHelper) {
		this.rabbitAdapterHelper = rabbitAdapterHelper;
		return this;
	}

	public RabbitAdapter setRabbitProperties(RabbitProperties rabbitProperties) {
		this.rabbitProperties = rabbitProperties;
		this.cachingConnectionFactory = rabbitProperties;

		LOG.info(String.format("rabbitmq client [%s] started", rabbitProperties.getKey()));
		return this;
	}

	@Override
	public void send(MQMessage msg) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("send a message , maname is [%s]", msg.getmQName()));
		}
		rabbitTemplate.convertAndSend(msg.getmQName(), msg.getContent());
	}

	@Override
	public void listener(final IMQReceiver mQReceiver) {
		rabbitAdapterHelper.initListener(mQReceiver, cachingConnectionFactory);
	}
}

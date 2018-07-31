package com.jimistore.boot.nemo.mq.rabbitmq.adapter;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import com.jimistore.boot.nemo.mq.core.adapter.IMQProperties;

public class RabbitProperties extends CachingConnectionFactory implements IMQProperties {

	String type;
	
	String key;

	public String getType() {
		return type;
	}

	public RabbitProperties setType(String type) {
		this.type = type;
		return this;
	}

	public String getKey() {
		return key;
	}

	public RabbitProperties setKey(String key) {
		this.key = key;
		return this;
	}
	
}

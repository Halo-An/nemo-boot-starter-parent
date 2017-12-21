package com.jimistore.boot.nemo.mq.rabbitmq.adapter;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitDataSource {
	
	public static final String DEFAULT = "default";
	
	String type;
	
	String key;
	
	RabbitTemplate rabbitTemplate;

	public String getType() {
		return type;
	}

	public RabbitDataSource setType(String type) {
		this.type = type;
		return this;
	}

	public String getKey() {
		return key;
	}

	public RabbitDataSource setKey(String key) {
		this.key = key;
		return this;
	}

	public RabbitTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}

	public RabbitDataSource setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		return this;
	}

}

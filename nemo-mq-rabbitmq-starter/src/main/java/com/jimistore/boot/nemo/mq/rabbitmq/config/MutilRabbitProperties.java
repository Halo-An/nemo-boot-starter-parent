package com.jimistore.boot.nemo.mq.rabbitmq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jimistore.boot.nemo.mq.rabbitmq.adapter.RabbitProperties;


@ConfigurationProperties("nemo.mq")
public class MutilRabbitProperties {
	
	Map<String, RabbitProperties> rabbitmq = new HashMap<String, RabbitProperties>();

	public Map<String, RabbitProperties> getRabbitmq() {
		return rabbitmq;
	}

	public MutilRabbitProperties setRabbitmq(Map<String, RabbitProperties> rabbitmq) {
		this.rabbitmq = rabbitmq;
		return this;
	}

}

package com.jimistore.boot.nemo.mq.rocketmq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jimistore.boot.nemo.mq.rocketmq.adapter.RocketMQProperties;

@ConfigurationProperties("nemo.mq")
public class MutilRocketMQProperties {
	
	public static final String ROCKETMQ  = "rocketmq";

	Map<String, RocketMQProperties> rocketmq = new HashMap<String, RocketMQProperties>();

	public Map<String, RocketMQProperties> getRocketmq() {
		return rocketmq;
	}

	public MutilRocketMQProperties setRocketmq(Map<String, RocketMQProperties> rocketmq) {
		this.rocketmq = rocketmq;
		return this;
	}
	
}

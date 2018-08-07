package com.jimistore.boot.nemo.mq.activemq.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jimistore.boot.nemo.mq.activemq.adapter.MyActiveMQProperties;

@ConfigurationProperties("nemo.mq")
public class MutilActiveMQProperties {
	
	public static final String MQ_DATASOURCE_TYPE_ACTIVEMQ = "activemq";
	
	Map<String, MyActiveMQProperties> activemq = new HashMap<String, MyActiveMQProperties>();

	public Map<String, MyActiveMQProperties> getActivemq() {
		return activemq;
	}

	public MutilActiveMQProperties setActivemq(Map<String, MyActiveMQProperties> activemq) {
		this.activemq = activemq;
		return this;
	}
	
}

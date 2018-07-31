package com.jimistore.boot.nemo.mq.activemq.adapter;

import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;

import com.jimistore.boot.nemo.mq.core.adapter.IMQProperties;

public class MyActiveMQProperties extends ActiveMQProperties implements IMQProperties {
	
	String type;
	
	String key;

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return key;
	}

	public MyActiveMQProperties setType(String type) {
		this.type = type;
		return this;
	}

	public MyActiveMQProperties setKey(String key) {
		this.key = key;
		return this;
	}

}

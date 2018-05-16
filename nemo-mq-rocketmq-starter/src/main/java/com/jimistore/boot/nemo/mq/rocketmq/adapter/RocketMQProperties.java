package com.jimistore.boot.nemo.mq.rocketmq.adapter;

import com.jimistore.boot.nemo.mq.core.adapter.IMQProperties;

public class RocketMQProperties implements IMQProperties {
	
	String type;
	
	String key;
	
	String url;
	
	String user;
	
	String password;
	
	String producerId;
	
	String consumerId;
	
	Long sendTimeOut=3000l;

	public String getUrl() {
		return url;
	}

	public RocketMQProperties setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getUser() {
		return user;
	}

	public RocketMQProperties setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RocketMQProperties setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getProducerId() {
		return producerId;
	}

	public RocketMQProperties setProducerId(String producerId) {
		this.producerId = producerId;
		return this;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public RocketMQProperties setConsumerId(String consumerId) {
		this.consumerId = consumerId;
		return this;
	}

	public Long getSendTimeOut() {
		return sendTimeOut;
	}

	public RocketMQProperties setSendTimeOut(Long sendTimeOut) {
		this.sendTimeOut = sendTimeOut;
		return this;
	}

	public String getType() {
		return type;
	}

	public RocketMQProperties setType(String type) {
		this.type = type;
		return this;
	}

	public String getKey() {
		return key;
	}

	public RocketMQProperties setKey(String key) {
		this.key = key;
		return this;
	}
	
}
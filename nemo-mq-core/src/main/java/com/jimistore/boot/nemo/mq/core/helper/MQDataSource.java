package com.jimistore.boot.nemo.mq.core.helper;

import com.jimistore.boot.nemo.mq.core.adapter.IMQDataSource;
import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;

public class MQDataSource implements IMQDataSource {
	
	public static final String DEFAULT = "default";
	
	String type;
	
	String key;
	
	IMQSender sender;
	
	IMQListener listener;
	

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getKey() {
		return key;
	}

	public MQDataSource setType(String type) {
		this.type = type;
		return this;
	}

	public MQDataSource setKey(String key) {
		this.key = key;
		return this;
	}

	public IMQSender getSender() {
		return sender;
	}

	public MQDataSource setSender(IMQSender sender) {
		this.sender = sender;
		return this;
	}

	public IMQListener getListener() {
		return listener;
	}

	public MQDataSource setListener(IMQListener listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public IMQSender getMQSender() {
		return sender;
	}

	@Override
	public IMQListener getMQListener() {
		return listener;
	}
	
	
}

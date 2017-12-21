package com.jimistore.boot.nemo.mq.activemq.adapter;

import org.springframework.jms.core.JmsMessagingTemplate;

/**
 * 用来存储jms协议mq的数据源
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class JMSDataSource {
	
	public static final String DEFAULT = "default";
	
	String key;
	
	String type;
	
	JmsMessagingTemplate jmsMessagingTemplate;

	public String getKey() {
		return key;
	}

	public JMSDataSource setKey(String key) {
		this.key = key;
		return this;
	}

	public String getType() {
		return type;
	}

	public JMSDataSource setType(String type) {
		this.type = type;
		return this;
	}

	public JmsMessagingTemplate getJmsMessagingTemplate() {
		return jmsMessagingTemplate;
	}

	public JMSDataSource setJmsMessagingTemplate(JmsMessagingTemplate jmsMessagingTemplate) {
		this.jmsMessagingTemplate = jmsMessagingTemplate;
		return this;
	}

}

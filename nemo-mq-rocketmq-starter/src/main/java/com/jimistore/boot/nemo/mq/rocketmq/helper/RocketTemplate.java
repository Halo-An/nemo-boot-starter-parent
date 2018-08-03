package com.jimistore.boot.nemo.mq.rocketmq.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.jimistore.boot.nemo.mq.core.enums.QueueType;
import com.jimistore.boot.nemo.mq.rocketmq.adapter.RocketMQProperties;

public class RocketTemplate implements InitializingBean, DisposableBean {
	
	RocketMQProperties rocketMQProperties;
	
	Producer producer;
	
	Consumer consumer;
	
	Map<String, MessageListener> map = new HashMap<String, MessageListener>();

	public RocketTemplate setRocketMQProperties(RocketMQProperties rocketMQProperties) {
		this.rocketMQProperties = rocketMQProperties;
		return this;
	}
	
	public RocketTemplate send(String mqname, QueueType type, Object message){
		Message msg = new Message(mqname, mqname, UUID.randomUUID().toString(), message.toString().getBytes());
		producer.send(msg);
		return this;
	}
	
	public RocketTemplate registerMessageListener(String mqname, MessageListener messageListener){
		map.put(mqname, messageListener);
		return this;
	}

	@Override
	public void destroy() throws Exception {
		this.shutdown();
	}
	
	public void shutdown(){
		producer.shutdown();
		consumer.shutdown();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		Properties properties = new Properties();
        // 您在控制台创建的 Consumer ID
        properties.put(PropertyKeyConst.ConsumerId, rocketMQProperties.getConsumerId());
		//您在控制台创建的 Producer ID
        properties.put(PropertyKeyConst.ProducerId, rocketMQProperties.getProducerId());
        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey,rocketMQProperties.getUser());
        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, rocketMQProperties.getPassword());
        //设置发送超时时间，单位毫秒
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, rocketMQProperties.getSendTimeOut().toString());
        // 设置 TCP 接入域名（此处以公共云生产环境为例）
        properties.put(PropertyKeyConst.ONSAddr, rocketMQProperties.getUrl());
        
        producer = ONSFactory.createProducer(properties);
		
        consumer = ONSFactory.createConsumer(properties);
        for(Entry<String, MessageListener> entry:map.entrySet()){
    		consumer.subscribe(entry.getKey(), "*", entry.getValue());
        }
		
		producer.start();
		consumer.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public void run() {
				shutdown();
			}
			
		});
		
	}
	
	

}

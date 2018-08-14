package com.jimistore.boot.nemo.mq.rocketmq.adapter;

import java.util.Properties;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.adapter.MQMessage;

public class RocketAdapter implements IMQAdapter, DisposableBean {
	
	private static final Logger log = Logger.getLogger(RocketAdapter.class);
	
	RocketMQProperties rocketMQProperties;
	
	Producer producer;
	
	Consumer consumer;

	public RocketMQProperties getRocketMQProperties() {
		return rocketMQProperties;
	}

	public RocketAdapter setRocketMQProperties(RocketMQProperties rocketMQProperties) {
		this.rocketMQProperties = rocketMQProperties;
		
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

		log.debug("create rocketmq producer");
        producer = ONSFactory.createProducer(properties);
		log.debug("rocketmq producer created");

		log.debug("create rocketmq consumer");
        consumer = ONSFactory.createConsumer(properties);
		
		
		log.debug("rocketmq client starting");
		
		producer.start();
		consumer.start();
		
		log.info(String.format("rocketmq client [%s] started", rocketMQProperties.getKey()));
		
		Runtime.getRuntime().addShutdownHook(new Thread(){

			@Override
			public void run() {
				shutdown();
			}
			
		});
		return this;
	}

	@Override
	public void send(MQMessage message) {
		Message msg = new Message(message.getmQName(), message.getmQName(), UUID.randomUUID().toString(), message.getContent().toString().getBytes());
		msg.setTag(message.getTag());
		if(message.getDelayTime()>0){
			msg.setStartDeliverTime(System.currentTimeMillis()+message.getDelayTime());
		}
		producer.send(msg);
	}
	
	@Override
	public void listener(final IMQReceiver mQReceiver) {
		
		consumer.subscribe(mQReceiver.getmQName(), mQReceiver.getTag(),new MessageListener(){
			@Override
			public Action consume(Message message, ConsumeContext context) {
				// TODO Auto-generated method stub
				try {
					mQReceiver.receive(new String(message.getBody()));
					return Action.CommitMessage;
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Action.ReconsumeLater;
			}
			
		});
	}
	
	@Override
	public void destroy() throws Exception {
		this.shutdown();
	}
	
	public void shutdown(){
		log.debug("rocketmq client shutdowning");
		
		producer.shutdown();
		consumer.shutdown();
		
		log.debug("rocketmq client shutdowned");
	}
}

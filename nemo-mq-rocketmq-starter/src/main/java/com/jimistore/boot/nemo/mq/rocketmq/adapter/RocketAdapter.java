package com.jimistore.boot.nemo.mq.rocketmq.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.order.ConsumeOrderContext;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderAction;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.adapter.MQMessage;
import com.jimistore.boot.nemo.mq.rocketmq.enums.RocketMQType;

public class RocketAdapter implements IMQAdapter, MessageListener, MessageOrderListener {

	private static final Logger LOG = LoggerFactory.getLogger(RocketAdapter.class);

	RocketMQProperties rocketMQProperties;

	Producer producer;

	OrderProducer orderProducer;

	Consumer consumer;

	OrderConsumer orderConsumer;

	RocketMQType type;

	Map<String, Map<String, IMQReceiver>> receiverMap = new HashMap<String, Map<String, IMQReceiver>>();

	public RocketMQProperties getRocketMQProperties() {
		return rocketMQProperties;
	}

	public RocketAdapter setRocketMQProperties(RocketMQProperties rocketMQProperties) {
		this.rocketMQProperties = rocketMQProperties;

		Properties properties = new Properties();
		// AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.AccessKey, rocketMQProperties.getUser());
		// SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
		properties.put(PropertyKeyConst.SecretKey, rocketMQProperties.getPassword());
		// 设置 TCP 接入域名（此处以公共云生产环境为例）
		properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMQProperties.getUrl());
		// 设置RocketMQ的其它配置
		if (rocketMQProperties.getExtend() != null) {
			for (Entry<String, String> entry : rocketMQProperties.getExtend().entrySet()) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}

		type = RocketMQType.parse(rocketMQProperties.getType());

		if (rocketMQProperties.getProducerId() != null) {
			LOG.debug("create rocketmq producer");
			// 您在控制台创建的 Producer ID
			properties.put(PropertyKeyConst.GROUP_ID, rocketMQProperties.getProducerId());
			// 设置发送超时时间，单位毫秒
			properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis,
					rocketMQProperties.getSendTimeOut().toString());

			switch (type) {
			case ORDER:
				orderProducer = ONSFactory.createOrderProducer(properties);
				orderProducer.start();
				break;
			default:
				producer = ONSFactory.createProducer(properties);
				producer.start();
				break;
			}
			LOG.debug("rocketmq producer created");
		}

		if (rocketMQProperties.getConsumerId() != null) {
			LOG.debug("create rocketmq consumer");
			// 您在控制台创建的 Consumer ID
			properties.put(PropertyKeyConst.GROUP_ID, rocketMQProperties.getConsumerId());

			switch (type) {
			case ORDER:
				orderConsumer = ONSFactory.createOrderedConsumer(properties);
				orderConsumer.start();
				break;
			default:
				consumer = ONSFactory.createConsumer(properties);
				consumer.start();
				break;
			}
			LOG.debug("rocketmq rocketmq created");

			LOG.info(String.format("rocketmq client [%s] started", rocketMQProperties.getKey()));

			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					shutdown();
				}

			});
		}

		return this;
	}

	@Override
	public void send(MQMessage message) {
		if (producer == null && orderProducer == null) {
			throw new RuntimeException(String.format("can not find producer id for topic[%s]", message.getmQName()));
		}
		if (message == null) {
			throw new RuntimeException("message cannot be empty");
		}
		if (StringUtils.isEmpty(message.getKey())) {
			message.setKey(UUID.randomUUID().toString());
		}

		Message msg = new Message(message.getmQName(), message.getmQName(), message.getKey(),
				message.getContent().toString().getBytes());
		msg.setTag(message.getTag());
		if (message.getDelayTime() > 0) {
			msg.setStartDeliverTime(System.currentTimeMillis() + message.getDelayTime());
		}
		if (!StringUtils.isEmpty(message.getKey())) {
			msg.setKey(message.getKey());
		}
		this.send(msg, message.getShardingKey());
	}

	@Override
	public void listener(final IMQReceiver mQReceiver) {
		if (consumer == null && orderConsumer == null) {
			throw new RuntimeException(String.format("can not find consumer id for topic[%s]", mQReceiver.getmQName()));
		}

		String key = mQReceiver.getmQName();
		if (!receiverMap.containsKey(key)) {
			receiverMap.put(key, new HashMap<String, IMQReceiver>());
		}
		Map<String, IMQReceiver> tagMap = receiverMap.get(key);
		tagMap.put(mQReceiver.getTag(), mQReceiver);
		Set<String> keySet = tagMap.keySet();
		List<String> keyList = new ArrayList<>(keySet);
		Collections.sort(keyList);
		StringBuilder tags = new StringBuilder();
		for (String tag : keyList) {
			if (tags.length() > 0) {
				tags.append(" || ");
			}
			tags.append(tag);
		}
		switch (type) {
		case ORDER:
			orderConsumer.subscribe(mQReceiver.getmQName(), tags.toString(), this);
			break;
		default:
			consumer.subscribe(mQReceiver.getmQName(), tags.toString(), this);
			break;
		}
	}

	public void shutdown() {
		LOG.debug("rocketmq client shutdowning");

		if (producer != null) {
			producer.shutdown();
		}
		if (orderProducer != null) {
			orderProducer.shutdown();
		}
		if (consumer != null) {
			consumer.shutdown();
		}
		if (orderConsumer != null) {
			orderConsumer.shutdown();
		}

		LOG.debug("rocketmq client shutdowned");
	}

	public void start() {
		LOG.debug("rocketmq client start");

		if (producer != null) {
			producer.start();
		}
		if (orderProducer != null) {
			orderProducer.start();
		}
		if (consumer != null) {
			consumer.start();
		}
		if (orderConsumer != null) {
			orderConsumer.start();
		}

		LOG.debug("rocketmq client start");
	}

	@Override
	public Action consume(Message message, ConsumeContext context) {
		try {
			IMQReceiver mQReceiver = receiverMap.get(message.getTopic()).get(message.getTag());
			mQReceiver.receive(new String(message.getBody()));
			return Action.CommitMessage;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
		return Action.ReconsumeLater;
	}

	@Override
	public OrderAction consume(Message message, ConsumeOrderContext context) {
		try {
			IMQReceiver mQReceiver = receiverMap.get(message.getTopic()).get(message.getTag());
			mQReceiver.receive(new String(message.getBody()));
			return OrderAction.Success;
		} catch (Throwable e) {
			LOG.error(e.getMessage(), e);
		}
		return OrderAction.Suspend;
	}

	private void send(Message msg, String shardingKey) {

		switch (type) {
		case ORDER:
			if (StringUtils.isEmpty(shardingKey)) {
				shardingKey = String.valueOf(System.currentTimeMillis());
			}
			orderProducer.send(msg, shardingKey);
			break;
		default:
			producer.send(msg);
			break;
		}

	}
}

package com.jimistore.boot.nemo.mq.activemq.adapter;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsMessagingTemplate;

import com.jimistore.boot.nemo.mq.activemq.helper.JMSAdapterHelper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.enums.QueueType;

/**
 * jms协议的适配器
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class JMSAdapter implements IMQAdapter {
	
	private static final Logger log = Logger.getLogger(JMSAdapter.class);

	MyActiveMQProperties myActiveMQProperties;
	
	JmsMessagingTemplate jmsMessagingTemplate;
	
	ActiveMQConnectionFactory activeMQConnectionFactory;
	
	JMSAdapterHelper jMSAdapterHelper;

	public JMSAdapter setjMSAdapterHelper(JMSAdapterHelper jMSAdapterHelper) {
		this.jMSAdapterHelper = jMSAdapterHelper;
		return this;
	}



	public JMSAdapter setMyActiveMQProperties(MyActiveMQProperties myActiveMQProperties) {
		this.myActiveMQProperties = myActiveMQProperties;
		
		activeMQConnectionFactory = new ActiveMQConnectionFactory(
				myActiveMQProperties.getUser(),
				myActiveMQProperties.getPassword(),
				myActiveMQProperties.getBrokerUrl());
		
		activeMQConnectionFactory.setUseAsyncSend(true);
		
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
		pooledConnectionFactory.setMaxConnections(100);
		
		jmsMessagingTemplate = new JmsMessagingTemplate();
		jmsMessagingTemplate.setConnectionFactory(pooledConnectionFactory);
		
		log.info(String.format("activemq client [%s] started", myActiveMQProperties.getKey()));
		return this;
	}



	@Override
	public void send(String dataSource, String mqname, QueueType type, Object msg) {
		if(log.isDebugEnabled()){
			log.debug(String.format("send a message , maname is [%s]", mqname));
		}
		jmsMessagingTemplate.getJmsTemplate().setPubSubDomain(QueueType.Topic.equals(type));
		jmsMessagingTemplate.convertAndSend(mqname, msg);
	}

	@Override
	public void listener(final IMQReceiver mQReceiver) {
		jMSAdapterHelper.initListener(mQReceiver, activeMQConnectionFactory);
	}

}

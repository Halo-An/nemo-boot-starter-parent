package com.jimistore.boot.nemo.mq.activemq.adapter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.MessageCreator;

import com.jimistore.boot.nemo.mq.activemq.helper.JMSAdapterHelper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.adapter.MQMessage;
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
		return this;
	}
	
	public JMSAdapter init(){
		
		activeMQConnectionFactory = new ActiveMQConnectionFactory(
				myActiveMQProperties.getUser(),
				myActiveMQProperties.getPassword(),
				myActiveMQProperties.getBrokerUrl());
		
		activeMQConnectionFactory.setUseAsyncSend(true);
		
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory);
		
		if(myActiveMQProperties.getPool()!=null){
			ActiveMQProperties.Pool pool = myActiveMQProperties.getPool();
			pooledConnectionFactory.setBlockIfSessionPoolIsFull(pool.isBlockIfFull());
			pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(pool.getBlockIfFullTimeout());
			pooledConnectionFactory.setCreateConnectionOnStartup(pool.isCreateConnectionOnStartup());
			pooledConnectionFactory.setExpiryTimeout(pool.getExpiryTimeout());
			pooledConnectionFactory.setIdleTimeout(pool.getIdleTimeout());
			pooledConnectionFactory.setMaxConnections(pool.getMaxConnections());
			pooledConnectionFactory.setMaximumActiveSessionPerConnection(pool.getMaximumActiveSessionPerConnection());
			pooledConnectionFactory.setReconnectOnException(pool.isReconnectOnException());
			pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(pool.getTimeBetweenExpirationCheck());
			pooledConnectionFactory.setUseAnonymousProducers(pool.isUseAnonymousProducers());			
		}
		
		jmsMessagingTemplate = new JmsMessagingTemplate();
		jmsMessagingTemplate.setConnectionFactory(pooledConnectionFactory);
		
		log.info(String.format("activemq client [%s] started", myActiveMQProperties.getKey()));
		
		return this;
	}



	@Override
	public void send(MQMessage msg) {
		if(log.isDebugEnabled()){
			log.debug(String.format("send a message , maname is [%s]", msg.getmQName()));
		}
		jmsMessagingTemplate.getJmsTemplate().setPubSubDomain(QueueType.Topic.equals(msg.getQueueType()));
		jmsMessagingTemplate.getJmsTemplate().send(msg.getmQName(), new MessageCreator(){
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(msg.getContent().toString());
				if(msg.getDelayTime()>0){
					message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, msg.getDelayTime());
				}
				return message;
			}
			
		});
	}

	@Override
	public void listener(final IMQReceiver mQReceiver) {
		jMSAdapterHelper.initListener(mQReceiver, activeMQConnectionFactory);
	}

}

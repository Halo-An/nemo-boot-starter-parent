package com.jimistore.boot.nemo.mq.activemq.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.enums.QueueType;

/**
 * jms协议的适配器
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class JMSAdapter implements IMQAdapter, BeanFactoryPostProcessor {
	
	private static final Logger log = Logger.getLogger(JMSAdapter.class);
		
	private DefaultListableBeanFactory dlbf;
	
	private Map<String, JMSDataSource> dataSourceMap = new HashMap<String, JMSDataSource>();
	
	private List<IMQReceiver> mQReceiverList = new ArrayList<IMQReceiver>();

	public JMSAdapter setmQDataSourceList(List<JMSDataSource> mQDataSourceList) {
		for(JMSDataSource mQDataSource:mQDataSourceList){
			dataSourceMap.put(mQDataSource.getKey(), mQDataSource);
		}
		return this;
	}

	@Override
	public void send(String dataSource, String mqname, QueueType type, Object msg) {
		log.info(String.format("send a message , maname is [%s]", mqname));
		JmsMessagingTemplate jmsMessagingTemplate = getmQDataSource(dataSource).getJmsMessagingTemplate();
		jmsMessagingTemplate.getJmsTemplate().setPubSubDomain(QueueType.Topic.equals(type));
		jmsMessagingTemplate.convertAndSend(mqname, msg);
	}

	@Override
	public void listener(final IMQReceiver mQReceiver) {
		mQReceiverList.add(mQReceiver);
		this.initListener();
	}
	
	private ConnectionFactory getConnectionFactory(String key){
		return this.getmQDataSource(key).getJmsMessagingTemplate().getConnectionFactory();
	}
	
	private JMSDataSource getmQDataSource(String key){
		if(!dataSourceMap.containsKey(key)){
			throw new RuntimeException(String.format("cannot find config of datasource[%s], check application.properties please ", key));
		}
		return dataSourceMap.get(key);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.dlbf = (DefaultListableBeanFactory) beanFactory;
		this.initListener();
	}
	
	private void initListener(){
		if(this.dlbf==null){
			return ;
		}
		for(int i=mQReceiverList.size()-1;i>=0;i--){
			final IMQReceiver mQReceiver = mQReceiverList.get(i);
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
	                .rootBeanDefinition(DefaultMessageListenerContainer.class)
	                .addPropertyValue("connectionFactory", this.getConnectionFactory(mQReceiver.getmQDataSource()))
	                .addPropertyValue("destinationName", mQReceiver.getmQName())
	                .addPropertyValue("sessionTransacted", true)
	                .addPropertyValue("cacheLevel", DefaultMessageListenerContainer.CACHE_NONE)
	                .addPropertyValue("messageListener", new SessionAwareMessageListener<TextMessage>(){

						@Override
						public void onMessage(TextMessage message, Session session) throws JMSException {
							try {
								log.info(String.format("receive a message, mqname is [%s]", mQReceiver.getmQName()));
								mQReceiver.receive(message.getText());
							} catch (Throwable e) {
								log.warn("handle massage throw a exception", e);
								throw new JMSException(e.getMessage(), e.toString());
							}
						}
	                	
	                });

			log.info(String.format("add a message listener[%s]", mQReceiver.getmQName()));
			dlbf.registerBeanDefinition(String.format("mq-%s-clientProxy", mQReceiver.getmQName()), beanDefinitionBuilder.getBeanDefinition());
			
			mQReceiverList.remove(i);
		}
		
	}
	

}

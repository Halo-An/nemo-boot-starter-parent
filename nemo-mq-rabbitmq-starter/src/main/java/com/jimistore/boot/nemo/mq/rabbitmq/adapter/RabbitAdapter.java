package com.jimistore.boot.nemo.mq.rabbitmq.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.jimistore.boot.nemo.mq.core.adapter.IMQAdapter;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.enums.QueueType;

public class RabbitAdapter implements IMQAdapter, BeanFactoryPostProcessor {
	
	private static final Logger log = Logger.getLogger(RabbitAdapter.class);
		
	private DefaultListableBeanFactory dlbf;
	
	private Map<String, RabbitDataSource> dataSourceMap = new HashMap<String, RabbitDataSource>();
	
	private List<IMQReceiver> mQReceiverList = new ArrayList<IMQReceiver>();

	@Override
	public void send(String dataSource, String mqname, QueueType type, Object msg) {
		this.getRabbitDataSource(dataSource).getRabbitTemplate().convertAndSend(mqname, msg);
	}
	
	public RabbitAdapter setmQDataSourceList(List<RabbitDataSource> mQDataSourceList) {
		for(RabbitDataSource mQDataSource:mQDataSourceList){
			dataSourceMap.put(mQDataSource.getKey(), mQDataSource);
		}
		return this;
	}
	
	@Override
	public void listener(final IMQReceiver mQReceiver) {
		mQReceiverList.add(mQReceiver);
		this.initListener();
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.dlbf = (DefaultListableBeanFactory) beanFactory;
		this.initListener();
	}
	
	private RabbitDataSource getRabbitDataSource(String key){
		RabbitDataSource rabbitDataSource = dataSourceMap.get(key);
		if(rabbitDataSource==null){
			throw new RuntimeException(String.format("cannot find config of datasource[%s], check application.properties please ", key));
		}
		return rabbitDataSource;
	}
	
	private void initListener(){
		if(this.dlbf==null){
			return ;
		}
		for(int i=mQReceiverList.size()-1;i>=0;i--){
			final IMQReceiver mQReceiver = mQReceiverList.get(i);
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
	                .rootBeanDefinition(SimpleMessageListenerContainer.class)
	                .addPropertyValue("connectionFactory", this.getRabbitDataSource(mQReceiver.getmQDataSource())
	                		.getRabbitTemplate().getConnectionFactory())
	                .addPropertyValue("queueNames", mQReceiver.getmQName())
	                .addPropertyValue("messageListener", new MessageListener(){

						@Override
						public void onMessage(Message message) {
							try {
								log.info(String.format("receive a message, mqname is [%s]", mQReceiver.getmQName()));
								mQReceiver.receive(new String(message.getBody()));
							} catch (Throwable e) {
								log.warn("handle massage throw a exception", e);
								if(e instanceof RuntimeException){
									throw (RuntimeException)e;
								}else{
									throw new RuntimeException(e);
								}
							}
						}
	                	
	                })
	                ;
			
			log.info(String.format("add a message listener[%s]", mQReceiver.getmQName()));
			dlbf.registerBeanDefinition(String.format("mq-%s-clientProxy", mQReceiver.getmQName()), beanDefinitionBuilder.getBeanDefinition());
		}
		
	}
}

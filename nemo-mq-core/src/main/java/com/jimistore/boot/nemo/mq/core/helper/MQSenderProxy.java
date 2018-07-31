package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.support.RemoteAccessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.adapter.MQMessage;
import com.jimistore.boot.nemo.mq.core.enums.QueueType;

/**
 * 每个mq发送端的代理
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class MQSenderProxy extends RemoteAccessor implements IMQSender,MethodInterceptor,InitializingBean,FactoryBean<Object> {
	
	private static final Logger log = Logger.getLogger(MQSenderProxy.class);
	
	IMQSender mQSender;
	
	String dataSource;
	
	String mQGroup;
	
	QueueType type;
		
	Object proxyObject;
	
	ObjectMapper objectMapper;
	
	ApplicationContext applicationContext;
	
	AsynExecuter asynExecuter;
	
	public MQSenderProxy setAsynExecuter(AsynExecuter asynExecuter) {
		this.asynExecuter = asynExecuter;
		return this;
	}

	public MQSenderProxy setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public MQSenderProxy setmQSender(IMQSender mQSender) {
		this.mQSender = mQSender;
		return this;
	}

	public MQSenderProxy setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public MQSenderProxy setmQGroup(String mQGroup) {
		this.mQGroup = mQGroup;
		return this;
	}

	@Override
	public void send(MQMessage msg) {
		log.debug("request send of MQSenderProxy");
		if(asynExecuter!=null){
			asynExecuter.execute(new IExecuter(){
				@Override
				public void execute() {
					mQSender.send(msg);
				}
			});
		}else{
			mQSender.send(msg);
		}
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		List<String> msgList = new ArrayList<String>();
		for(Object obj:invocation.getArguments()){
			msgList.add(objectMapper.writeValueAsString(obj));
		}
		String msgStr = objectMapper.writeValueAsString(msgList);
		MQMessage msg = new MQMessage()
				.setDataSource(dataSource)
				.setQueueType(type)
				.setmQName(this.getMQNameByMethod(invocation.getMethod()))
				.setContent(msgStr);
		this.send(msg);
		return null;
	}
	
	private String getMQNameByMethod(Method method){
		return MQNameHelper.getMQNameByGroupAndMethod(this.getServiceInterface().getName(), mQGroup, method);
	}

	@Override
	public Object getObject() throws Exception {
		return proxyObject;
	}

	@Override
	public Class<?> getObjectType() {
		return this.getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		proxyObject = ProxyFactory.getProxy(this.getServiceInterface(), this);
	}

}

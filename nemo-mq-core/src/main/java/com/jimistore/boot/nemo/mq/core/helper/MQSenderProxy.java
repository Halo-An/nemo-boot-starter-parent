package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.remoting.support.RemoteAccessor;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.core.util.AnnotationUtil;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.adapter.MQMessage;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQMapping;

/**
 * 每个mq发送端的代理
 * 
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class MQSenderProxy extends RemoteAccessor
		implements IMQSender, MethodInterceptor, InitializingBean, FactoryBean<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(MQSenderProxy.class);

	IMQSender mQSender;

	MQNameHelper mQNameHelper;

	Object proxyObject;

	ObjectMapper objectMapper;

	ApplicationContext applicationContext;

	AsynExecuter asynExecuter;

	public MQSenderProxy setAsynExecuter(AsynExecuter asynExecuter) {
		this.asynExecuter = asynExecuter;
		return this;
	}

	public MQSenderProxy setmQNameHelper(MQNameHelper mQNameHelper) {
		this.mQNameHelper = mQNameHelper;
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

	@Override
	public void send(MQMessage msg) {
		LOG.debug("request send of MQSenderProxy");
		if (asynExecuter != null) {
			asynExecuter.execute(new IExecuter() {
				@Override
				public void execute() {
					mQSender.send(msg);
				}
			});
		} else {
			mQSender.send(msg);
		}
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		List<String> msgList = new ArrayList<String>();
		for (Object obj : invocation.getArguments()) {
			msgList.add(objectMapper.writeValueAsString(obj));
		}
		String msgStr = objectMapper.writeValueAsString(msgList);

		JsonMQMapping destination = AnnotationUtil.getAnnotation(invocation.getMethod(), JsonMQMapping.class);
		String mQName = this.getMQNameByMethod(invocation.getMethod());

		StandardEvaluationContext context = SpelUtil.getContextByProceedingJoinPoint(invocation);
		Long delayTime = SpelUtil.parseExpression(context, destination.delay(), Long.class);
		String key = destination.key();
		String shardingKey = destination.shardingKey();
		if (!StringUtils.isEmpty(key)) {
			key = SpelUtil.parseExpression(context, key, String.class);
		}
		if (!StringUtils.isEmpty(shardingKey)) {
			shardingKey = SpelUtil.parseExpression(context, shardingKey, String.class);
		}

		MQMessage msg = new MQMessage().setQueueType(destination.type())
				.setmQName(mQName)
				.setKey(key)
				.setShardingKey(shardingKey)
				.setTag(destination.tag())
				.setDelayTime(delayTime)
				.setContent(msgStr);
		this.send(msg);
		return null;
	}

	private String getMQNameByMethod(Method method) {
		return mQNameHelper.getMQNameClassAndMethod(this.getServiceInterface(), method);
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

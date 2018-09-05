package com.jimistore.boot.nemo.sliding.window.helper;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.jimistore.boot.nemo.sliding.window.annotation.Publish;
import com.jimistore.boot.nemo.sliding.window.core.PublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.Topic;
import com.jimistore.util.reflex.AnnotationUtil;

@Aspect
@Order(15)
public class PublishAspect {
	
	private static final Logger log = Logger.getLogger(PublishAspect.class);
	
	private PublisherHelper publisherHelper;

	public PublishAspect setPublisherHelper(PublisherHelper publisherHelper) {
		this.publisherHelper = publisherHelper;
		return this;
	}


	@Pointcut("@annotation(com.jimistore.boot.nemo.sliding.window.annotation.Publish)")
	public void publish(){
	}
	
	@Around("publish()")
	public Object aroundProduce(ProceedingJoinPoint joinPoint) throws Throwable{
		Throwable throwable = null;
		Object result = null;
		try{
			Signature signature = joinPoint.getSignature();
			MethodSignature methodSignature = (MethodSignature) signature;
			Method method = methodSignature.getMethod();
			
			try{
				result=joinPoint.proceed(joinPoint.getArgs());
			}catch(Throwable e){
				throwable = e;
			}
			List<Topic> topicList = this.getTopicList(method);
			
			for(Topic topic:topicList){
				
				StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, result, throwable);
				String key = this.parseExpression(context, topic.getKey(), String.class);
				boolean condition = this.parseExpression(context, topic.getCondition(), Boolean.class);
				int num = this.parseExpression(context, topic.getNum(), Integer.class);
				
				log.debug(String.format("check topic[%s], the condition is %s=%s, the result is %s, the error is %s", key, topic.getCondition(), condition, result, throwable));
				publisherHelper.createCounter(topic);
				if(condition){
					log.debug(String.format("publish counter %s", key));
					publisherHelper.publish(new PublishEvent<Integer>()
							.setTime(System.currentTimeMillis())
							.setTopicKey(key)
							.setValue(num)
							);
				}
			}
		}catch(Throwable t){
			log.warn(t.getMessage(), t);
		}
			
		if(throwable!=null){
			throw throwable;
		}
		
		return result;
	}
	
	
	/**
	 * spel格式化
	 * @param context
	 * @param str
	 * @return
	 */
	private <T> T parseExpression(StandardEvaluationContext context,String str, Class<T> clazz){
		return new SpelExpressionParser().parseExpression(str).getValue(context, clazz);
	}
	
	/**
	 * 获取函数spel对应初始化的上下文
	 * @param joinPoint
	 * @return
	 */
	private StandardEvaluationContext getContextByProceedingJoinPoint(ProceedingJoinPoint joinPoint, Object result, Throwable error){
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		Object[] objs = joinPoint.getArgs();
		StandardEvaluationContext context = new StandardEvaluationContext();
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		for(int i=0;i<objs.length;i++){
			context.setVariable(String.format("%s%s", "p", i), objs[i]);
		}
		for(int i=0;i<objs.length;i++){
			context.setVariable(parameterNames[i], objs[i]);
		}
		context.setVariable("result", result);
		context.setVariable("error", error);

		return context;
	}
	
	/**
	 * 
	 * @return
	 */
	protected List<Topic> getTopicList(Method method){
		List<Topic> topicList = new ArrayList<Topic>();
		Publish publish = AnnotationUtil.getAnnotation(method, Publish.class);
		for(com.jimistore.boot.nemo.sliding.window.annotation.Topic topic:publish.value()){
			topicList.add(Topic.from(topic));
		}
		
		if(publisherHelper!=null){
			topicList.addAll(publisherHelper.listTopicByPublisher(PublisherUtil.getPublisherKeyByMethod(method)));
		}
		return topicList;
	}
	
	
	
}


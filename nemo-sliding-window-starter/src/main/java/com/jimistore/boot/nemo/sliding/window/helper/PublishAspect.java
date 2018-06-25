package com.jimistore.boot.nemo.sliding.window.helper;


import java.lang.reflect.Method;

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
import com.jimistore.boot.nemo.sliding.window.annotation.Topic;
import com.jimistore.boot.nemo.sliding.window.core.PublishEvent;
import com.jimistore.util.reflex.AnnotationUtil;

@Aspect
@Order(13)
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
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		
		Throwable throwable = null;
		Object result = null;
		try{
			result=joinPoint.proceed(joinPoint.getArgs());
		}catch(Throwable e){
			throwable = e;
		}
		
		Publish publish = AnnotationUtil.getAnnotation(method, Publish.class);
		
		
		for(Topic topic:publish.value()){
			
			StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, result, throwable);
			String key = this.parseExpression(context, topic.value(), String.class);
			boolean condition = this.parseExpression(context, topic.condition(), Boolean.class);
			int num = this.parseExpression(context, topic.num(), Integer.class);
			
			log.debug(String.format("publish counter %s", key));
			
			publisherHelper.createCounter(key, topic.timeUnit(), topic.capacity(), topic.valueType());
			if(condition){
				publisherHelper.publish(new PublishEvent<Integer>()
						.setTime(System.currentTimeMillis())
						.setTopicKey(key)
						.setValue(num)
						);
			}
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
	
}


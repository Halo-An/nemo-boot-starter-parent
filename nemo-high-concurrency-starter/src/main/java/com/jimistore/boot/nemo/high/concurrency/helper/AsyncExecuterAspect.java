package com.jimistore.boot.nemo.high.concurrency.helper;


import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import com.cq.nemo.util.reflex.AnnotationUtil;
import com.jimistore.boot.nemo.high.concurrency.api.annotation.Async;

@Aspect
@Order(14)
public class AsyncExecuterAspect {
	
	private AsyncExecuterHelper asyncExecuterHelper;

	public AsyncExecuterAspect setAsyncExecuterHelper(AsyncExecuterHelper asyncExecuterHelper) {
		this.asyncExecuterHelper = asyncExecuterHelper;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.high.concurrency.api.annotation.Async)")
	public void syn(){
	}
	
	@Around("syn()")
	public Object aroundLock(ProceedingJoinPoint joinPoint) throws Throwable{
		
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		
		Async async = AnnotationUtil.getAnnotation(method, Async.class);
		
		String group = async.value();
		if(group==null||group.trim().length()==0){
			StringBuffer sb = new StringBuffer();
			sb.append(joinPoint.getTarget().getClass().getSimpleName());
			sb.append("-").append(method.getName());
			for(Class<?> paramType:method.getParameterTypes()){
				sb.append("-").append(paramType.getSimpleName());
			}
			group = sb.toString();
		}

		asyncExecuterHelper.execute(group, async.capacity(), new IExecuter() {
			
			@Override
			public void execute() {
				try {
					joinPoint.proceed();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		return null;
	}
}

package com.jimistore.boot.nemo.lock.helper;


import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.lock.annotation.Async;
import com.jimistore.util.reflex.AnnotationUtil;

@Aspect
@Order(14)
public class AsyncExecuterAspect {
	
	private AsyncExecuterHelper asyncExecuterHelper;

	public AsyncExecuterAspect setAsyncExecuterHelper(AsyncExecuterHelper asyncExecuterHelper) {
		this.asyncExecuterHelper = asyncExecuterHelper;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.lock.annotation.Async)")
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

		asyncExecuterHelper.execute(group, async.capacity(), async.maxCapacity(), async.queueCapacity(), new Thread(String.format("%s-%s", group, System.currentTimeMillis())) {
			
			@Override
			public void run() {
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

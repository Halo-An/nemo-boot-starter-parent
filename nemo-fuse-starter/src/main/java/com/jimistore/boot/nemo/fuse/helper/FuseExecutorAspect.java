package com.jimistore.boot.nemo.fuse.helper;


import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.fuse.annotation.Fuse;
import com.jimistore.boot.nemo.fuse.core.FuseTemplate;
import com.jimistore.boot.nemo.fuse.core.ITask;
import com.jimistore.util.reflex.AnnotationUtil;

@Aspect
@Order(15)
public class FuseExecutorAspect {
	
	private FuseTemplate fuseTemplate;
	
	public FuseExecutorAspect setFuseTemplate(FuseTemplate fuseTemplate) {
		this.fuseTemplate = fuseTemplate;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.fuse.annotation.Fuse)")
	public void syn(){
	}
	
	@Around("syn()")
	public Object aroundLock(ProceedingJoinPoint joinPoint) throws Throwable{
		
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		
		Fuse fuse = AnnotationUtil.getAnnotation(method, Fuse.class);
		
		String key = fuse.value();
		long timeout = fuse.timeout();
		
		
		return fuseTemplate.execute(key, new ITask<Object>() {

			@Override
			public Object call() throws Exception {
				try {
					return joinPoint.proceed();
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public long getTimeout() {
				return timeout;
			}

			@Override
			public Callable<Object> getCallback() {
				return null;
			}
			
		});
	}
}

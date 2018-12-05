package com.jimistore.boot.nemo.id.generator.helper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.core.util.AnnotationUtil;
import com.jimistore.boot.nemo.id.generator.annotation.IDGenerator;

@Aspect
@Order(Integer.MAX_VALUE)
public class IDGeneratorAspect {
	
	private final Logger log = Logger.getLogger(getClass());
	
	IDGeneratorHelper iDGeneratorHelper;

	public IDGeneratorAspect setiDGeneratorHelper(IDGeneratorHelper iDGeneratorHelper) {
		this.iDGeneratorHelper = iDGeneratorHelper;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.id.generator.annotation.IDGenerator)")
	public void gquery(){
	}
	
	@Around("gquery()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		log.debug("request query");
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		IDGenerator iDGenerator = AnnotationUtil.getAnnotation(method, IDGenerator.class);
		String id = iDGeneratorHelper.generator(iDGenerator);
		Object[] args = joinPoint.getArgs();
		Parameter[] params = method.getParameters();
		for(int i = 0;i<params.length;i++) {
			Parameter param = params[i];
			if(param.getType().equals(String.class)&&param.getName().equals(iDGenerator.field())) {
				args[i]=id;
			}
		}
		
		return joinPoint.proceed(args);
	}
	
	
	
}


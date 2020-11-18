package com.jimistore.boot.nemo.id.generator.helper;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.core.util.AnnotationUtil;
import com.jimistore.boot.nemo.id.generator.annotation.IDGenerator;

@Aspect
@Order(Integer.MAX_VALUE)
public class IDGeneratorAspect {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	IDGeneratorHelper iDGeneratorHelper;

	public IDGeneratorAspect setiDGeneratorHelper(IDGeneratorHelper iDGeneratorHelper) {
		this.iDGeneratorHelper = iDGeneratorHelper;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.id.generator.annotation.IDGenerator)")
	public void gquery() {
	}

	@Around("gquery()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		LOG.debug("request query");
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		IDGenerator iDGenerator = AnnotationUtil.getAnnotation(method, IDGenerator.class);
		String id = iDGeneratorHelper.generator(iDGenerator);
		Object[] args = joinPoint.getArgs();
		if (args.length <= iDGenerator.field()) {
			throw new RuntimeException("field of IDGenerator cannot be less than length of args");
		}
		args[iDGenerator.field()] = id;
		return joinPoint.proceed(args);
	}

}

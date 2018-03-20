package com.jimistore.boot.nemo.high.concurrency.helper;


import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.cq.nemo.util.reflex.AnnotationUtil;
import com.jimistore.boot.nemo.high.concurrency.api.annotation.Lock;

@Aspect
@Order(12)
public class LockAspect {
	
	private LockHelper lockHelper;

	public LockAspect setLockHelper(LockHelper lockHelper) {
		this.lockHelper = lockHelper;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.high.concurrency.api.annotation.Lock)")
	public void syn(){
	}
	
	@Around("syn()")
	public Object aroundLock(ProceedingJoinPoint joinPoint) throws Throwable{
		
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		Object[] objs = joinPoint.getArgs();
		
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		for(int i=0;i<objs.length;i++){
			context.setVariable(String.format("%s%s", "p", i), objs[i]);
		}
		for(int i=0;i<objs.length;i++){
			context.setVariable(parameterNames[i], objs[i]);
		}
		
		Lock lock = AnnotationUtil.getAnnotation(method, Lock.class);
		String subject = parser.parseExpression(lock.value()).getValue(context, String.class);
		String operator = String.format("%s.%s", joinPoint.getTarget().getClass().getName() ,method.getName());
		if(lock.operator()!=null&&lock.operator().trim().length()>0){
			operator = lock.operator();
		}
		lockHelper.lock(subject, operator, lock.timeout(), lock.prompt());  
		try{
			Object obj = joinPoint.proceed();
			return obj;
		}finally{
			lockHelper.unlock(subject, operator);
		}
		
		
	}
}

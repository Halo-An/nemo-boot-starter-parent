package com.jimistore.boot.nemo.lock.helper;

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
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.jimistore.boot.nemo.lock.annotation.LockConsume;
import com.jimistore.boot.nemo.lock.annotation.LockProduce;
import com.jimistore.boot.nemo.lock.annotation.LockStockCreate;
import com.jimistore.boot.nemo.lock.annotation.LockStockUpdate;

@Aspect
@Order(13)
public class StockAspect {

	private StockHelper stockHelper;

	public StockAspect setStockHelper(StockHelper stockHelper) {
		this.stockHelper = stockHelper;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.lock.annotation.LockConsume)")
	public void consume() {
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.lock.annotation.LockProduce)")
	public void produce() {
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.lock.annotation.LockStockUpdate)")
	public void update() {
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.lock.annotation.LockStockCreate)")
	public void create() {
	}

	@Around("produce()")
	public Object aroundProduce(ProceedingJoinPoint joinPoint) throws Throwable {
		Object obj = joinPoint.proceed();

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		LockProduce lock = method.getAnnotation(LockProduce.class);
		StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, obj);
		String key = this.parseExpression(context, lock.key(), String.class);
		Long num = this.parseExpression(context, lock.num(), Long.class);

		stockHelper.produce(key, num);
		return obj;
	}

	@Around("create()")
	public Object aroundCreate(ProceedingJoinPoint joinPoint) throws Throwable {

		Object obj = joinPoint.proceed();

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		LockStockCreate lock = method.getAnnotation(LockStockCreate.class);
		StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, obj);
		String key = this.parseExpression(context, lock.key(), String.class);
		Long num = this.parseExpression(context, lock.num(), Long.class);
		stockHelper.create(key, num, lock.timeout());
		return obj;
	}

	@Around("update()")
	public Object aroundUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
		Object obj = joinPoint.proceed();

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		LockStockUpdate lock = method.getAnnotation(LockStockUpdate.class);
		StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, obj);
		String key = this.parseExpression(context, lock.key(), String.class);
		Long num = this.parseExpression(context, lock.num(), Long.class);

		stockHelper.cover(key, num, lock.timeout());
		return obj;
	}

	@Around("consume()")
	public Object aroundConsume(ProceedingJoinPoint joinPoint) throws Throwable {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		LockConsume lock = method.getAnnotation(LockConsume.class);
		StandardEvaluationContext context = this.getContextByProceedingJoinPoint(joinPoint, null);
		String key = this.parseExpression(context, lock.key(), String.class);
		Long num = this.parseExpression(context, lock.num(), Long.class);

		stockHelper.consume(key, num, lock.prompt());
		return joinPoint.proceed();
	}

	/**
	 * spel格式化
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	private <T> T parseExpression(StandardEvaluationContext context, String str, Class<T> clazz) {
		return new SpelExpressionParser().parseExpression(str).getValue(context, clazz);
	}

	/**
	 * 获取函数spel对应初始化的上下文
	 * 
	 * @param joinPoint
	 * @return
	 */
	private StandardEvaluationContext getContextByProceedingJoinPoint(ProceedingJoinPoint joinPoint, Object returnObj) {
		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		Object[] objs = joinPoint.getArgs();
		StandardEvaluationContext context = new StandardEvaluationContext();
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		context.setVariable("result", returnObj);
		for (int i = 0; i < objs.length; i++) {
			context.setVariable(String.format("%s%s", "p", i), objs[i]);
		}
		for (int i = 0; i < objs.length; i++) {
			context.setVariable(parameterNames[i], objs[i]);
		}
		return context;
	}

}

package com.jimistore.boot.nemo.fuse.helper;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

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

import com.jimistore.boot.nemo.fuse.annotation.Fuse;
import com.jimistore.boot.nemo.fuse.core.FuseTemplate;
import com.jimistore.boot.nemo.fuse.core.ITask;

@Aspect
@Order(15)
public class FuseExecutorAspect {

	private FuseTemplate fuseTemplate;

	public FuseExecutorAspect setFuseTemplate(FuseTemplate fuseTemplate) {
		this.fuseTemplate = fuseTemplate;
		return this;
	}

	@Pointcut("@annotation(com.jimistore.boot.nemo.fuse.annotation.Fuse)")
	public void syn() {
	}

	@Around("syn()")
	public Object aroundLock(ProceedingJoinPoint joinPoint) throws Throwable {

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		Fuse fuse = method.getAnnotation(Fuse.class);

		String key = fuse.value();
		long timeout = fuse.timeout();

		if (key.trim().length() == 0) {
			StringBuilder sb = new StringBuilder("fuse-").append(joinPoint.getTarget().getClass().getName())
					.append(":")
					.append(method.getName());
			sb.append("(");
			for (Class<?> paramType : method.getParameterTypes()) {
				sb.append(paramType.getName()).append(",");
			}
			if (method.getParameterTypes().length > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(")");
			key = sb.toString();
		} else {
			Object[] objs = joinPoint.getArgs();
			ExpressionParser parser = new SpelExpressionParser();
			StandardEvaluationContext context = new StandardEvaluationContext();
			ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
			String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
			for (int i = 0; i < objs.length; i++) {
				context.setVariable(String.format("%s%s", "p", i), objs[i]);
			}
			for (int i = 0; i < objs.length; i++) {
				context.setVariable(parameterNames[i], objs[i]);
			}
			key = parser.parseExpression(key).getValue(context, String.class);
		}

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

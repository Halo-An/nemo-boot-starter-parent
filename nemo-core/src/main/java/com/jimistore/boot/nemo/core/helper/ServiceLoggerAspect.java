package com.jimistore.boot.nemo.core.helper;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.core.util.JsonString;

@Order(101)
@Aspect
public class ServiceLoggerAspect {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Pointcut("@within(org.springframework.stereotype.Service)")
	public void logService() {
	}

	public static final class Log {
		String time;
		long process;
		String clazz;
		String method;
		String user;
		String thread;
		long start;
		long end;
		Object request;
		Object response;
		Object error;

		public String getTime() {
			return time;
		}

		public Log setTime(String time) {
			this.time = time;
			return this;
		}

		public long getProcess() {
			return end - start;
		}

		public Log setProcess(long process) {
			this.process = process;
			return this;
		}

		public String getClazz() {
			return clazz;
		}

		public Log setClazz(String clazz) {
			this.clazz = clazz;
			return this;
		}

		public String getMethod() {
			return method;
		}

		public Log setMethod(String method) {
			this.method = method;
			return this;
		}

		public String getUser() {
			return user;
		}

		public Log setUser(String user) {
			this.user = user;
			return this;
		}

		public String getThread() {
			return thread;
		}

		public Log setThread(String thread) {
			this.thread = thread;
			return this;
		}

		public long getEnd() {
			return end;
		}

		public Log setEnd(long end) {
			this.end = end;
			return this;
		}

		public Object getRequest() {
			return request;
		}

		public Log setRequest(Object request) {
			this.request = request;
			return this;
		}

		public Object getResponse() {
			return response;
		}

		public Log setResponse(Object response) {
			this.response = response;
			return this;
		}

		public Object getError() {
			return error;
		}

		public Log setError(Object error) {
			this.error = error;
			return this;
		}

		public long getStart() {
			return start;
		}

		public Log setStart(long start) {
			this.start = start;

			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(start);
			this.time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(c.getTime());
			return this;
		}

	}

	public ThreadLocal<Log> tLog = new ThreadLocal<Log>();

	@Before("logService()")
	public void before(JoinPoint joinPoint) throws Throwable {
		if (!LOGGER.isTraceEnabled()) {
			return;
		}

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		StringBuilder sb = new StringBuilder();
		for (Class<?> paramClazz : method.getParameterTypes()) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(paramClazz.getSimpleName());
		}
		String methodName = sb.insert(0, "(").insert(0, method.getName()).append(")").toString();

		// 接收到请求，记录请求内容
		Thread thread = Thread.currentThread();

		Log log = new Log();
		log.setClazz(joinPoint.getTarget().getClass().getName());
		log.setMethod(methodName);
		log.setUser((String) Context.get(Context.CONTEXT_REQUEST_USER));
		log.setThread(new StringBuilder(thread.getName()).append("-").append(thread.getId()).toString());
		log.setRequest(joinPoint.getArgs());
		log.setStart(System.currentTimeMillis());
		tLog.set(log);
	}

	@AfterReturning(returning = "response", pointcut = "logService()")
	public void doAfterReturning(Object response) throws Throwable {
		if (!LOGGER.isTraceEnabled()) {
			return;
		}
		tLog.get().setEnd(System.currentTimeMillis());
		tLog.get().setResponse(response);
		LOGGER.trace(JsonString.toJson(tLog.get()));
	}

	@AfterThrowing(pointcut = "logService()", throwing = "e")
	public void doThrowing(Exception e) throws Throwable {
		if (!LOGGER.isTraceEnabled()) {
			return;
		}
		tLog.get().setEnd(System.currentTimeMillis());
		tLog.get().setError(e.getMessage());
		LOGGER.trace(JsonString.toJson(tLog.get()));
	}

}

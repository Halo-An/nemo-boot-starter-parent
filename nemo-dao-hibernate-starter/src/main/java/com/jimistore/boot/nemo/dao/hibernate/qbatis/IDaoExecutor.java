package com.jimistore.boot.nemo.dao.hibernate.qbatis;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInvocation;

public interface IDaoExecutor {

	/**
	 * 处理的注解的class
	 * 
	 * @return
	 */
	public Class<? extends Annotation> getAnnotationClass();

	/**
	 * 执行
	 * 
	 * @param invocation
	 * @return
	 */
	public Object execute(MethodInvocation invocation);

}

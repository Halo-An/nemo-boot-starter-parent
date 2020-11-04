package com.jimistore.boot.nemo.dao.hibernate.qbatis;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInvocation;

import com.jimistore.boot.nemo.dao.hibernate.annotation.Execute;

public class DaoExecutor implements IDaoExecutor {

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return Execute.class;
	}

	@Override
	public Object execute(MethodInvocation invocation) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.jimistore.boot.nemo.dao.hibernate.qbatis;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

import com.jimistore.boot.nemo.dao.api.exception.DaoException;

public class DaoProxyFactory implements FactoryBean<Object>, MethodInterceptor {

	List<IDaoExecutor> daoExecutorList;

	Class<?> daoInterface;

	public DaoProxyFactory setDaoInterface(Class<?> daoInterface) {
		this.daoInterface = daoInterface;
		return this;
	}

	public DaoProxyFactory setDaoExecutorList(List<IDaoExecutor> daoExecutorList) {
		this.daoExecutorList = daoExecutorList;
		return this;
	}

	@Override
	public Object getObject() throws Exception {
		return ProxyFactory.getProxy(daoInterface, this);
	}

	@Override
	public Class<?> getObjectType() {
		return daoInterface;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		invocation.proceed();
		for (IDaoExecutor executor : daoExecutorList) {
			if (invocation.getMethod().isAnnotationPresent(executor.getAnnotationClass())) {
				return executor.execute(invocation);
			}
		}
		throw new DaoException("找不到对应数据处理的注解");
	}

}

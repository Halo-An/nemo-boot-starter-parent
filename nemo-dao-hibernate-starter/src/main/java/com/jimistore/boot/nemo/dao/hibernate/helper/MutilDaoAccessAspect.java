package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

import com.cq.nemo.util.reflex.AnnotationUtil;
import com.jimistore.boot.nemo.dao.hibernate.annotation.DataSource;

@Aspect
@Order(100)
public class MutilDaoAccessAspect {
	
	private final Logger log = Logger.getLogger(getClass());
	
	Map<Class<?>, DataSource> buffer = new HashMap<Class<?>, DataSource>();
	
	MutilSessionFactory mutilSessionFactory;


	public MutilDaoAccessAspect setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	@Pointcut("@within(com.jimistore.boot.nemo.dao.hibernate.annotation.DataSource)")
	public void dao(){
	}
	
	@Before("dao()")
	public void before(JoinPoint joinPoint) throws Throwable {
		Class<?> clazz = joinPoint.getTarget().getClass();
		DataSource dataSource = buffer.get(clazz);
		if(dataSource==null){
			dataSource = AnnotationUtil.getAnnotation(clazz, DataSource.class);
			buffer.put(clazz, dataSource);
		}
		if(log.isDebugEnabled()){
			log.debug(String.format("init datasource key[%s]", dataSource!=null?dataSource.value():"null"));
		}
		if(dataSource!=null){
			mutilSessionFactory.setDataSourceKey(dataSource.value());
		}
	}
	
	
	
}


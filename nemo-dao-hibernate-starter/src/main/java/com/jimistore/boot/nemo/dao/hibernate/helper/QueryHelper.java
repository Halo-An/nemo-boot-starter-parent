package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import com.jimistore.boot.nemo.dao.hibernate.annotation.SpelQuery;
import com.jimistore.boot.nemo.dao.hibernate.dao.MutilHibernateQueryDao;
import com.jimistore.boot.nemo.dao.hibernate.enums.QueryType;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;

public class QueryHelper {
	
	MutilHibernateQueryDao mutilHibernateQueryDao;
	
	IInjectSqlValidator injectSqlValidator;
	
	
	
	public QueryHelper setMutilHibernateQueryDao(MutilHibernateQueryDao mutilHibernateQueryDao) {
		this.mutilHibernateQueryDao = mutilHibernateQueryDao;
		return this;
	}



	public QueryHelper setInjectSqlValidator(IInjectSqlValidator injectSqlValidator) {
		this.injectSqlValidator = injectSqlValidator;
		return this;
	}



	public Object query(SpelQuery query, Method method, Object... params){
		
		
		SpelExpressionParser spel = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		for(int i=0;i<params.length;i++){
			context.setVariable(String.format("%s%s", "p", i), params[i]);
			context.setVariable(parameterNames[i], params[i]);
		}
		String hql = spel.parseExpression(query.value()).getValue(context, String.class);
		int pageSize = spel.parseExpression(query.pageSize()).getValue(context, Integer.class);
		int pageNum = spel.parseExpression(query.pageNum()).getValue(context, Integer.class);
		Class<?> returnType = method.getReturnType();
		ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
		Type[] types = pt.getActualTypeArguments();
		Class<?> entityClass = null;
		if(types!=null&&types.length==1){
			if(types[0] instanceof Class){
				entityClass = (Class<?>)types[0];
			}else if(types[0] instanceof ParameterizedType){
				entityClass = (Class<?>)((ParameterizedType)types[0]).getRawType();
			}
		}
		if(!List.class.isAssignableFrom(returnType)){throw new RuntimeException("return type must be List.class");}
		
		//sql注入校验
		String[] items = query.value().split("\\+");
		for(int i=0;i<items.length;i++){
			if(i%2==1){
				String item = spel.parseExpression(items[i]).getValue(context, String.class);
				injectSqlValidator.check(item);
			}
		}
		
		if(query.type().equals(QueryType.HQL)){
			//判断是否查询默认的全表
			if(StringUtils.isEmpty(hql)&&entityClass!=null){
				hql = String.format("from %s", entityClass.getName());
			}
			return mutilHibernateQueryDao.query(hql, pageNum, pageSize);
		}
		return mutilHibernateQueryDao.queryBySql(hql, pageNum, pageSize, entityClass);
	}
	
	

}

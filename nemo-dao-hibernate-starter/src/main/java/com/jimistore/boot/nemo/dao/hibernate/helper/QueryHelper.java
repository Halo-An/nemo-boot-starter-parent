package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import com.jimistore.boot.nemo.dao.hibernate.annotation.SpelQuery;
import com.jimistore.boot.nemo.dao.hibernate.dao.MutilHibernateQueryDao;
import com.jimistore.boot.nemo.dao.hibernate.enums.QueryType;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;

public class QueryHelper {
	
	private static final Logger log = Logger.getLogger(QueryHelper.class);
	
	MutilHibernateQueryDao mutilHibernateQueryDao;
	
	IInjectSqlValidator injectSqlValidator;
	
	public static final char[] NORMAL_CHAR = {
			'0','1','2','3','4','5','6','7','8','9',
			'a','b','c','d','e','f','g',
			'h','i','j','k','l','m','n',
			'o','p','q','r','s','t',
			'u','v','w','x','y','z',
			'_','(',')','.'
	};
	
	public static final Set<Character> NORMAL_CHAR_SET= new HashSet<Character>();
	
	static {
		for(Character str:NORMAL_CHAR){
			NORMAL_CHAR_SET.add(str);
		}
	}
	
	
	
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
		StringBuilder hqlSpel = new StringBuilder();
		for(String hqlItem:query.value()){
			hqlSpel.append(hqlItem);
		}
		String hql = spel.parseExpression(hqlSpel.toString()).getValue(context, String.class);
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
		for(String paramSpel:this.parseParamKeys(hqlSpel.toString())){
			try{
				String param = spel.parseExpression(paramSpel).getValue(context, String.class);
				injectSqlValidator.check(param);
			}catch(SpelEvaluationException e){
				if(log.isDebugEnabled()){
					log.warn(String.format("spel parse error, the el is \"%s\" , the error msg is \"%s\"", paramSpel, e.getMessage()));
				}
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
	
	private Set<String> parseParamKeys(String str){
		Set<String> set = new HashSet<>();
		int start=0,end=0;
		while(true){
			start = str.indexOf("#", end);
			if(start<0||start>str.length()){
				break;
			}
			for(end=start+1;end<str.length();end++){
				Character cha= str.charAt(end);
				if(!NORMAL_CHAR_SET.contains(cha)){
					set.add(str.substring(start, end));
					start = end;
					break;
				}
			}
		}
		return set;
	}
	

}

package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOG = LoggerFactory.getLogger(QueryHelper.class);

	MutilHibernateQueryDao mutilHibernateQueryDao;

	List<IInjectSqlValidator> queryValidatorList;

	private List<ISpelExtendFunc> spelExtendFuncList;

	public static final char[] NORMAL_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', '_', '.'

	};

	public static final char[] INC_CHAR = { '(', ')' };

	public static final char[] SENCOND_CHAR = { '#', '"', '\'', '$', '%', ',', ' ' };

	private static final Set<Character> NORMAL_CHAR_SET = new HashSet<Character>();

	Set<String> funcSet = new HashSet<String>();

	static {
		for (Character str : NORMAL_CHAR) {
			NORMAL_CHAR_SET.add(str);
		}
	}

	public QueryHelper setSpelExtendFuncList(List<ISpelExtendFunc> spelExtendFuncList) {
		this.spelExtendFuncList = spelExtendFuncList;
		if (spelExtendFuncList != null) {
			for (ISpelExtendFunc func : spelExtendFuncList) {
				funcSet.add(String.format("#%s", func.getKey()));
			}
		}
		return this;
	}

	public QueryHelper setMutilHibernateQueryDao(MutilHibernateQueryDao mutilHibernateQueryDao) {
		this.mutilHibernateQueryDao = mutilHibernateQueryDao;
		return this;
	}

	public QueryHelper setQueryValidatorList(List<IInjectSqlValidator> queryValidatorList) {
		this.queryValidatorList = queryValidatorList;
		return this;
	}

	public Object query(SpelQuery query, Method method, Object... params) {

		// ?????????spel???context
		SpelExpressionParser spel = new SpelExpressionParser();
		StandardEvaluationContext context = this.initContext(method, params);

		// ??????spel?????????
		String hql = this.parseSql(query, spel, context);
		int pageSize = spel.parseExpression(query.pageSize()).getValue(context, Integer.class);
		int pageNum = spel.parseExpression(query.pageNum()).getValue(context, Integer.class);

		// ?????????????????????
		if (query.update()) {
			return mutilHibernateQueryDao.execute(hql);
		}

		// ??????????????????
		Class<?> returnType = method.getReturnType();
		Class<?> entityClass = this.parseEntityClass(method);
		return this.loadData(query, hql, pageNum, pageSize, returnType, entityClass);
	}

	/**
	 * ?????????spel?????????
	 * 
	 * @param method
	 * @param params
	 * @return
	 */
	private StandardEvaluationContext initContext(Method method, Object... params) {
		StandardEvaluationContext context = new StandardEvaluationContext();
		ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
		String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
		if (spelExtendFuncList != null) {
			for (ISpelExtendFunc func : spelExtendFuncList) {
				context.setVariable(func.getKey(), func);
			}
		}
		for (int i = 0; i < params.length; i++) {
			context.setVariable(String.format("%s%s", "p", i), params[i]);
			context.setVariable(parameterNames[i], params[i]);
		}
		return context;
	}

	/**
	 * ??????????????????
	 * 
	 * @param spel
	 * @param context
	 * @param hqlSpel
	 * @return
	 */
	private String parseSql(SpelQuery query, SpelExpressionParser spel, StandardEvaluationContext context) {
		// ????????????
		StringBuilder hqlSpel = new StringBuilder();
		for (String hqlItem : query.value()) {
			hqlSpel.append(hqlItem);
		}
		// ??????spel?????????
		String hql = spel.parseExpression(hqlSpel.toString()).getValue(context, String.class);
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("execute spel.parseExpression, result is \"%s\" ", hql));
		}

		for (String paramSpel : this.parseParamKeys(hqlSpel.toString())) {
			try {
				String param = spel.parseExpression(paramSpel).getValue(context, String.class);
				// ????????????????????????????????????sql????????????
				String[] items = paramSpel.split("\\.");
				if (!funcSet.contains(items[0])) {
					for (IInjectSqlValidator injectSqlValidator : queryValidatorList) {
						injectSqlValidator.check(param);
					}
				}
			} catch (SpelEvaluationException e) {
				LOG.warn(String.format("spel parse error, the el is \"%s\" , the error msg is \"%s\"", paramSpel,
						e.getMessage()));
			}
		}
		return hql;
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @param method
	 * @return
	 */
	private Class<?> parseEntityClass(Method method) {
		Class<?> entityClass = method.getReturnType();
		if (List.class.isAssignableFrom(entityClass)) {
			ParameterizedType pt = (ParameterizedType) method.getGenericReturnType();
			Type[] types = pt.getActualTypeArguments();
			if (types != null && types.length == 1) {
				if (types[0] instanceof Class) {
					entityClass = (Class<?>) types[0];
				} else if (types[0] instanceof ParameterizedType) {
					entityClass = (Class<?>) ((ParameterizedType) types[0]).getRawType();
				}
			}
		}
		return entityClass;
	}

	/**
	 * ????????????
	 * 
	 * @param query
	 * @param hql
	 * @param pageNum
	 * @param pageSize
	 * @param entityClass
	 * @return
	 */
	private Object loadData(SpelQuery query, String hql, int pageNum, int pageSize, Class<?> returnType,
			Class<?> entityClass) {
		List<?> dataList = null;
		if (query.type().equals(QueryType.HQL)) {
			// ?????????????????????????????????
			if (StringUtils.isEmpty(hql) && entityClass != null) {
				hql = String.format("from %s", entityClass.getName());
			}
			dataList = mutilHibernateQueryDao.query(hql, pageNum, pageSize);
		}
		dataList = mutilHibernateQueryDao.queryBySql(hql, pageNum, pageSize, entityClass);

		if (List.class.isAssignableFrom(returnType)) {
			return dataList;
		}
		if (dataList != null && dataList.size() > 0) {
			return dataList.get(0);
		}
		return null;
	}

	/**
	 * ?????????????????????spel?????????
	 * 
	 * @param str
	 * @return
	 */
	private Set<String> parseParamKeys(String str) {
		Set<String> set = new HashSet<>();
		int start = 0, end = 0;
		while (true) {
			start = str.indexOf("#", end);
			if (start < 0 || start > str.length()) {
				break;
			}
			LinkedList<String> list = new LinkedList<String>();
			for (end = start + 1; end < str.length(); end++) {
				Character cha = str.charAt(end);
				if (list.size() == 0) {
					if (cha.equals(INC_CHAR[0])) {
						list.addLast("(");
					} else if (!NORMAL_CHAR_SET.contains(Character.toLowerCase(cha))) {
						set.add(str.substring(start, end));
						start = end;
						break;
					}
				} else {
					if (cha.equals(INC_CHAR[0])) {
						list.addLast("(");
					} else if (cha.equals(INC_CHAR[1])) {
						list.removeLast();
					}
				}

				if (end == str.length() - 1) {
					set.add(str.substring(start));
					start = end;
					break;
				}
			}
		}
		return set;
	}

}

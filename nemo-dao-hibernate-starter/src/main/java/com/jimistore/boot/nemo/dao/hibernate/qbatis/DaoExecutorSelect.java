package com.jimistore.boot.nemo.dao.hibernate.qbatis;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.jimistore.boot.nemo.core.util.AnnotationUtil;
import com.jimistore.boot.nemo.dao.hibernate.annotation.Select;
import com.jimistore.boot.nemo.dao.hibernate.dao.MutilHibernateQueryDao;

public class DaoExecutorSelect implements IDaoExecutor {

	DaoExecutorHelper daoExecutorHelper;

	List<IDaoSpelFunc> daoSpelFuncList;

	MutilHibernateQueryDao mutilHibernateQueryDao;

	public DaoExecutorSelect setDaoExecutorHelper(DaoExecutorHelper daoExecutorHelper) {
		this.daoExecutorHelper = daoExecutorHelper;
		return this;
	}

	public DaoExecutorSelect setDaoSpelFuncList(List<IDaoSpelFunc> daoSpelFuncList) {
		this.daoSpelFuncList = daoSpelFuncList;
		return this;
	}

	public DaoExecutorSelect setMutilHibernateQueryDao(MutilHibernateQueryDao mutilHibernateQueryDao) {
		this.mutilHibernateQueryDao = mutilHibernateQueryDao;
		return this;
	}

	@Override
	public Class<? extends Annotation> getAnnotationClass() {
		return Select.class;
	}

	@Override
	public Object execute(MethodInvocation invocation) {
		Method method = invocation.getMethod();
		Select anno = AnnotationUtil.getAnnotation(method, Select.class);

		StandardEvaluationContext context = daoExecutorHelper.initContext(method, daoSpelFuncList,
				invocation.getArguments());
		// 解析spel脚本
		String source = String.join(" ", anno.value());
		String sql = daoExecutorHelper.parseExpression(context, source, String.class);
		int pageSize = daoExecutorHelper.parseExpression(context, anno.pageSize(), Integer.class);
		int pageNum = daoExecutorHelper.parseExpression(context, anno.pageNum(), Integer.class);

		// 执行查询脚本
		Class<?> returnType = method.getReturnType();
		Class<?> entityClass = this.parseEntityClass(method);
		return this.loadData(sql, pageNum, pageSize, returnType, entityClass);
	}

	/**
	 * 解析需要查询的实体类
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
	 * 查询数据
	 * 
	 * @param query
	 * @param sql
	 * @param pageNum
	 * @param pageSize
	 * @param entityClass
	 * @return
	 */
	private <T> Object loadData(String sql, int pageNum, int pageSize, Class<?> returnType, Class<T> entityClass) {
		List<T> dataList = mutilHibernateQueryDao.queryBySql(sql, pageNum, pageSize, entityClass);

		if (List.class.isAssignableFrom(returnType)) {
			return dataList;
		} else if (Set.class.isAssignableFrom(returnType)) {
			Set<T> dataSet = new HashSet<>();
			dataSet.addAll(dataList);
			return dataSet;
		}

		if (dataList != null && dataList.size() > 0) {
			return dataList.get(0);
		}
		return null;
	}

}

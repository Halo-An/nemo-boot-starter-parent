package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;

import com.jimistore.boot.nemo.dao.api.enums.Compare;
import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.FilterEntry;
import com.jimistore.boot.nemo.dao.api.request.IQuery;
import com.jimistore.boot.nemo.dao.api.request.ITarget;
import com.jimistore.boot.nemo.dao.api.request.Order;
import com.jimistore.boot.nemo.dao.hibernate.request.SqlFunction;
import com.jimistore.boot.nemo.dao.hibernate.request.SqlOrder;
import com.jimistore.boot.nemo.dao.hibernate.request.SqlQuery;
import com.jimistore.boot.nemo.dao.hibernate.request.SqlTarget;

public class QueryParser implements IQueryParser {
	
	HibernateNamingStrategy hibernateNamingStrategy;

	public QueryParser setHibernateNamingStrategy(HibernateNamingStrategy hibernateNamingStrategy) {
		this.hibernateNamingStrategy = hibernateNamingStrategy;
		return this;
	}

	@Override
	public Query parse(Session session, IQuery<?> query) {
		if(query instanceof SqlQuery){
			return this.parseSql(session, (SqlQuery<?>) query);
		}else{
			return this.parseHql(session, query);
		}
	}

	/**
	 * 解析sql
	 * @param session
	 * @param query
	 * @return
	 */
	public Query parseSql(Session session, SqlQuery<?> query) {
		
		List<SqlTarget> sqlTargetList = new ArrayList<SqlTarget>();
		this.fillTargetList(query.getSqlTarget(), sqlTargetList);
		Map<SqlTarget,String> aliasMap = this.initAliasMap(sqlTargetList);
		
		//解析sql
		String selectSql = this.getSelectSql(query.getSqlTarget(), aliasMap , sqlTargetList);
		String targetSql = this.getFromSql(query.getSqlTarget(), aliasMap, sqlTargetList);
		String orderSql = this.getOrderSql(query, aliasMap);
		String sql = String.format("%s %s %s ", selectSql, targetSql, orderSql);	
		
		Query sQuery = session.createSQLQuery(sql);
		sQuery.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
		//设置分页
		if(query!=null&&query.getPageNum()!=null&&query.getPageSize()!=null){
			sQuery.setFirstResult((query.getPageNum() - 1) * query.getPageSize());
			sQuery.setMaxResults(query.getPageSize());
		}
		return sQuery;
	}

	/**
	 * 解析hql
	 * @param session
	 * @param query
	 * @return
	 */
	private Query parseHql(Session session, IQuery<?> query) {
		

		String targetSql = String.format("from %s", query.getTarget().getEntityClass().getSimpleName());
		String whereSql = this.getWhereSql(query.getTarget());
		String orderSql = this.getOrderSql(query);
		
		//解析hql
		String hql = String.format("%s %s %s", targetSql, whereSql, orderSql);
		
		org.hibernate.Query hQuery = session.createQuery(hql);
		
		//设置分页
		if(query!=null&&query.getPageNum()!=null&&query.getPageSize()!=null){
			hQuery.setFirstResult((query.getPageNum() - 1) * query.getPageSize());
			hQuery.setMaxResults(query.getPageSize());
		}
		return hQuery;
	}
	
	private Map<SqlTarget,String> initAliasMap(List<SqlTarget> sqlTargetList){

		Map<SqlTarget,String> aliasMap = new HashMap<SqlTarget,String>();
		
		for(int i=0;i<sqlTargetList.size();i++){
			SqlTarget sqlTarget = sqlTargetList.get(i);
			String alias = String.format("%s_%s", sqlTarget.getEntityClass().getSimpleName().toLowerCase(), i);
			aliasMap.put(sqlTarget, alias);
		}
		return aliasMap;
	}
	

	
	private String getSelectSql(SqlTarget target, Map<SqlTarget,String> aliasMap, List<SqlTarget> sqlTargetList){
		StringBuffer selectSql = new StringBuffer();
		for(int i=0;i<sqlTargetList.size();i++){
			SqlTarget sqlTarget = sqlTargetList.get(i);
			String alias = aliasMap.get(sqlTarget);
			if(sqlTarget.getOutFieldNames()!=null){
				for(String fieldName:sqlTarget.getOutFieldNames()){
					if(selectSql.length()>0){
						selectSql.append(", ");
					}else{
						selectSql.append("select ");
					}
					if(fieldName.indexOf("as ")>=0){
						//判断是否是聚合函数
						if(fieldName.indexOf("(")>=0&&fieldName.indexOf(")")>=0){
							selectSql.append(fieldName);
						}else{
							String column = fieldName.substring(0,fieldName.indexOf("as ")-1);
							column = this.getColumnByFieldName(column);
							String as = fieldName.substring(fieldName.indexOf("as "));
							selectSql.append(String.format("%s.%s %s", alias, column, as));
						}
					}else{
						String column = this.getColumnByFieldName(fieldName);
						selectSql.append(String.format("%s.%s as %s", alias, column, fieldName));
					}
				}
			}
		}
		
		if(selectSql.length()==0){
			throw new RuntimeException("columns of target cannot be empty.");
		}
		return selectSql.toString();
	}
	
	/**
	 * 根据SqlTarget解析sql
	 * @param target
	 * @return
	 */
	private String getFromSql(SqlTarget target, Map<SqlTarget,String> aliasMap, List<SqlTarget> sqlTargetList){
		StringBuffer fromSql = new StringBuffer();

		for(int i=0;i<sqlTargetList.size();i++){
			SqlTarget sqlTarget = sqlTargetList.get(i);
			String alias = aliasMap.get(sqlTarget);
			String entity = this.getTableNameByClass(sqlTarget.getEntityClass());
			if(entity==null){
				entity = sqlTarget.getEntityClass().getSimpleName();
			}
			String whereSql = this.getWhereSql(sqlTarget);
			if(fromSql.length()==0){
				fromSql.append("from ");
			}else{
				fromSql.append(" ").append(sqlTarget.getJoinType().getCode());
			}

			fromSql.append(String.format("(select * from %s %s ) %s ", entity, whereSql, alias));
			
			if(sqlTarget.getPreKey()!=null){
				String preAlias = aliasMap.get(sqlTarget.getPreTarget());
				String preColumn = this.getColumnByFieldName(sqlTarget.getPreKey());
				String selfColumn = this.getColumnByFieldName(sqlTarget.getSelfKey());
				
				fromSql.append(String.format(" on %s.%s=%s.%s", preAlias, preColumn, alias, selfColumn));
			}
		}
		
		return fromSql.toString();
		
	}

	/**
	 * 解析排序的sql
	 * @param query
	 * @return
	 */
	private String getOrderSql(IQuery<?> query, Map<SqlTarget,String> aliasMap){
		StringBuffer orderSql = new StringBuffer();
		
		//处理排序
		if(query.getOrders()!=null&&query.getOrders().length>0){
			
			for(Order order:query.getOrders()){
				if(orderSql.length()==0){
					orderSql.append(" order by ");
				}else{
					orderSql.append(",");
				}
				
				if(order instanceof SqlOrder){
					String column = this.getColumnByFieldName(order.getKey());
					String alias = aliasMap.get(((SqlOrder)order).getSqlTarget());
					orderSql.append(String.format("%s.%s %s", alias, column, order.getOrderType().getCode()));
				}else{
					orderSql.append(String.format("%s %s", order.getKey(), order.getOrderType().getCode()));
				}
			}
		}
		return orderSql.toString();
	}
	
	/**
	 * 
	 * @param target root
	 * @param sqlTargetList 目标集合
	 */
	private void fillTargetList(SqlTarget target, List<SqlTarget> sqlTargetList){
		sqlTargetList.add(target);
		
		if(target.getJoinList()!=null){
			for(SqlTarget sqlTarget:target.getJoinList()){
				sqlTarget.setPreTarget(target);
				this.fillTargetList(sqlTarget, sqlTargetList);
			}
		}
	}
	
	/**
	 * 解析过滤条件sql
	 * @param target
	 * @return
	 */
	private String getWhereSql(ITarget target){
		StringBuffer hql = new StringBuffer();
		//处理过滤条件
		Filter filter = target.getFilter();
		if(filter!=null){
			do{
				hql.append(String.format(" %s (", filter.getAndOr().getCode()));
				String joinStr = "";
				for(FilterEntry filterEntry:filter.getFilterEntrys()){
					hql.append(String.format("%s %s ", joinStr, this.getFilterEntrySql(target.getEntityClass(), filterEntry, target instanceof SqlTarget)));
					joinStr = filter.getChildAndOr().getCode();
				}
				hql.append(")");
			}while((filter=filter.getNext())!=null);
		}
		return hql.toString();
	}
	
	private String getOrderSql(IQuery<?> query){
		return this.getOrderSql(query, null);
//		StringBuffer hql = new StringBuffer();
//		//处理排序
//		if(query.getOrders()!=null&&query.getOrders().length>0){
//			
//			for(Order order:query.getOrders()){
//				if(hql.length()==0){
//					hql.append(" order by ");
//				}else{
//					hql.append(",");
//				}
//				String column = order.getKey();
//				if(order instanceof SqlOrder){
//					column = this.getColumnByFieldName(column);
//				}
//				hql.append(String.format("%s %s", column, order.getOrderType().getCode()));
//			}
//		}
//		return hql.toString();
	}
	
	/**
	 * 获取过滤条件sql参数
	 * @param entityClass
	 * @param filterEntry
	 * @return
	 */
	private Object getFilterEntrySql(Class<?> entityClass, FilterEntry filterEntry, boolean isSql){
		Class<?> fieldType = String.class;
		String column = null;
		Serializable key = filterEntry.getKey();
		if(key instanceof SqlFunction){
			SqlFunction sqlFunction = (SqlFunction) key;
			column = sqlFunction.getContent();
			fieldType = sqlFunction.getValueType();
		}else{
			column = key.toString();
			Field field = this.getField(entityClass, column);
			if(isSql){
				column = this.getColumnByFieldName(column);
			}
			if(field==null){
//				throw new RuntimeException(String.format("field not mapping: %s", filterEntry.getKey()));
			}else{
				fieldType = field.getType();
			}
		}
		
		
		if(filterEntry.getCompare().equals(Compare.like)){
			return new StringBuffer().append(column).append(" ").append(filterEntry.getCompare().getCode()).append(" '%").append(filterEntry.getValue()).append("%'").toString();
		}else if(filterEntry.getCompare().equals(Compare.nl)){
			return String.format("%s is null", column);
		}else if(filterEntry.getCompare().equals(Compare.nnl)){
			return String.format("%s is not null", column);
		}else if(fieldType.isArray()){
			StringBuffer sb =new StringBuffer(column).append(" ").append(filterEntry.getCompare().getCode()).append(" (");
			Object[] objs = (Object[])filterEntry.getValue();
			String joinStr="";
			for(Object obj:objs){
				sb.append(joinStr);
				joinStr=",";
				if(obj instanceof String){
					sb.append("'").append(obj).append("'");
				}else{
					sb.append(obj);
				}
			}
			sb.append(")");
			return sb.toString();
		}else if(fieldType.equals(String.class)){
			return String.format("%s %s '%s'", column, filterEntry.getCompare().getCode(), filterEntry.getValue());
		}else{
			return String.format("%s %s %s", column, filterEntry.getCompare().getCode(), filterEntry.getValue());
		}
	}
	
	/**
	 * 反射获取字段
	 * @param entityClass
	 * @param fieldName
	 * @return
	 */
	private Field getField(Class<?> entityClass, String fieldName){
		Field field = null;
		try {
			field = entityClass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException | SecurityException e) {
		}
		if(field==null&&entityClass.getSuperclass()!=null&&entityClass.getSuperclass()!=Object.class){
			return this.getField(entityClass.getSuperclass(), fieldName);
		}
		return field;
	}
	
	private String getTableNameByClass(Class<?> clazz){
		return hibernateNamingStrategy.classToTableName(clazz.getName());
	}
	
	private String getColumnByFieldName(String filedName){
		return hibernateNamingStrategy.propertyToColumnName(filedName);
	}
	

}

package com.jimistore.boot.nemo.dao.hibernate.helper;

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
		
		//解析sql
		String targetSql = this.getTargetSql(query.getSqlTarget());
		String orderSql = this.getOrderSql(query);
		String sql = String.format("%s %s", targetSql, orderSql);	
		
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
	
	/**
	 * 根据SqlTarget解析sql
	 * @param target
	 * @return
	 */
	private String getTargetSql(SqlTarget target){
		StringBuffer selectSql = new StringBuffer();
		StringBuffer fromSql = new StringBuffer();
		
		List<SqlTarget> sqlTargetList = new ArrayList<SqlTarget>();
		this.fillTargetList(target, sqlTargetList);
		
		Map<SqlTarget,String> aliasMap = new HashMap<SqlTarget,String>();

		int i=-1;
		for(SqlTarget sqlTarget:sqlTargetList){
			i++;
			String entity = this.getTableNameByClass(sqlTarget.getEntityClass());
			if(entity==null){
				entity = sqlTarget.getEntityClass().getSimpleName();
			}
			String alias = String.format("%s_%s", entity.toLowerCase(), i);
			aliasMap.put(sqlTarget, alias);
			if(sqlTarget.getOutFieldNames()!=null){
				for(String fieldName:sqlTarget.getOutFieldNames()){
					String column = this.getColumnByFieldName(fieldName);
					if(selectSql.length()>0){
						selectSql.append(", ");
					}else{
						selectSql.append("select ");
					}
					if(column.indexOf("as ")>=0){
						selectSql.append(column);
					}else{
						selectSql.append(alias).append(".").append(column);
					}
				}
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
		
		if(selectSql.length()==0){
			throw new RuntimeException("columns of target cannot be empty.");
		}
		
		
		return String.format("%s %s", selectSql.toString(), fromSql.toString());
		
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
	
	/**
	 * 解析排序的sql
	 * @param query
	 * @return
	 */
	private String getOrderSql(IQuery<?> query){
		StringBuffer hql = new StringBuffer();
		//处理排序
		if(query.getOrders()!=null&&query.getOrders().length>0){
			
			for(Order order:query.getOrders()){
				if(hql.length()==0){
					hql.append(" order by ");
				}else{
					hql.append(",");
				}
				String column = order.getKey();
				if(query.getTarget() instanceof SqlTarget){
					column = this.getColumnByFieldName(column);
				}
				hql.append(String.format("%s %s", column, order.getOrderType().getCode()));
			}
		}
		return hql.toString();
		
	}
	
	/**
	 * 获取过滤条件sql参数
	 * @param entityClass
	 * @param filterEntry
	 * @return
	 */
	private Object getFilterEntrySql(Class<?> entityClass, FilterEntry filterEntry, boolean isSql){
		Class<?> fieldType = String.class;
		String column = filterEntry.getKey();
		Field field = this.getField(entityClass, column);
		if(isSql){
			column = this.getColumnByFieldName(column);
		}
		if(field==null){
//			throw new RuntimeException(String.format("field not mapping: %s", filterEntry.getKey()));
		}else{
			fieldType = field.getType();
		}
		if(filterEntry.getCompare().equals(Compare.like)){
			return new StringBuffer().append(filterEntry.getCompare().getCode()).append(" '%").append(filterEntry.getValue()).append("%'").toString();
		}else if(filterEntry.getCompare().equals(Compare.nl)){
			return "is null";
		}else if(filterEntry.getCompare().equals(Compare.nnl)){
			return "is not null";
		}else if(fieldType.isArray()){
			StringBuffer sb =new StringBuffer(filterEntry.getCompare().getCode()).append(" (");
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

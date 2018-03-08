package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.lang.reflect.Field;

import org.hibernate.Session;

import com.jimistore.boot.nemo.dao.api.enums.Compare;
import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.FilterEntry;
import com.jimistore.boot.nemo.dao.api.request.Order;
import com.jimistore.boot.nemo.dao.api.request.Query;

public class QueryParser implements IQueryParser {

	@Override
	public org.hibernate.Query parse(Session session, Class<?> entityClass, Query query) {
		
		//解析hql
		String hql = this.getHql(entityClass, query);
		
		org.hibernate.Query hQuery = session.createQuery(hql);
		
		//设置分页
		if(query!=null&&query.getPageNum()!=null&&query.getPageSize()!=null){
			hQuery.setFirstResult((query.getPageNum() - 1) * query.getPageSize());
			hQuery.setMaxResults(query.getPageSize());
		}
		return hQuery;
	}
	
	private String getHql(Class<?> entityClass, Query query){
		StringBuffer hql = new StringBuffer(String.format("from %s", entityClass.getSimpleName()));
		//处理过滤条件
		Filter filter = query.getFilter();
		if(filter!=null){
			do{
				hql.append(String.format(" %s (", filter.getAndOr().getCode()));
				String joinStr = "";
				for(FilterEntry filterEntry:filter.getFilterEntrys()){
					hql.append(String.format("%s %s %s ", joinStr, filterEntry.getKey(), this.getValue(entityClass, filterEntry)));
					joinStr = filter.getChildAndOr().getCode();
				}
				hql.append(")");
			}while((filter=filter.getNext())!=null);
		}
		//处理排序
		if(query.getOrders()!=null&&query.getOrders().length>0){
			hql.append(" order by ");
			String joinStr = "";
			for(Order order:query.getOrders()){
				hql.append(String.format("%s %s %s", order.getKey(), order.getOrderType().getCode(), joinStr));
				joinStr=",";
			}
		}
		return hql.toString();
	}
	
	private Object getValue(Class<?> entityClass, FilterEntry filterEntry){
		Field field = this.getField(entityClass, filterEntry.getKey());
		if(field==null){
			throw new RuntimeException(String.format("field not mapping: %s", filterEntry.getKey()));
		}
		if(filterEntry.getCompare().equals(Compare.like)){
			return new StringBuffer().append(filterEntry.getCompare().getCode()).append(" '%").append(filterEntry.getValue()).append("%'").toString();
		}else if(filterEntry.getCompare().equals(Compare.nl)){
			return "is null";
		}else if(filterEntry.getCompare().equals(Compare.nnl)){
			return "is not null";
		}else if(field.getType().isArray()){
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
		}else if(field.getType().equals(String.class)){
			return String.format("%s '%s'", filterEntry.getCompare().getCode(), filterEntry.getValue());
		}else{
			return String.format("%s %s", filterEntry.getCompare().getCode(), filterEntry.getValue());
		}
	}
	
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
	

}

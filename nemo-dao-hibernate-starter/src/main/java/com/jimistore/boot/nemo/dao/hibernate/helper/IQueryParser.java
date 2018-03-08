package com.jimistore.boot.nemo.dao.hibernate.helper;

import org.hibernate.Session;

import com.jimistore.boot.nemo.dao.api.request.Query;

public interface IQueryParser {
	
	/**
	 * 根据过滤条件解析hibernate的query
	 * @param session
	 * @param entityClass
	 * @param filter
	 * @return
	 */
	public org.hibernate.Query parse(Session session, Class<?> entityClass, Query query);

}

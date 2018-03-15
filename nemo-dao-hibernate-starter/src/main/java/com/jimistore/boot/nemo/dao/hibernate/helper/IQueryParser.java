package com.jimistore.boot.nemo.dao.hibernate.helper;

import org.hibernate.Query;
import org.hibernate.Session;

import com.jimistore.boot.nemo.dao.api.request.IQuery;

public interface IQueryParser {
	
	/**
	 * 根据过滤条件解析hibernate的query
	 * @param session
	 * @param query
	 * @return
	 */
	public Query parse(Session session, IQuery<?> query);


}

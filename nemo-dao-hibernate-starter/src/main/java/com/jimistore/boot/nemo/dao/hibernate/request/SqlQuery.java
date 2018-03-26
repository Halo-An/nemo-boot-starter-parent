package com.jimistore.boot.nemo.dao.hibernate.request;

import com.jimistore.boot.nemo.dao.api.request.Order;
import com.jimistore.boot.nemo.dao.api.request.Query;

public class SqlQuery<T> extends Query<T> {
	
	public static final <T> SqlQuery<T> create(SqlTarget sqlTarget, Integer pageNum, Integer pageSize , Order... orders){
		return (SqlQuery<T>)new SqlQuery<T>().setTarget(sqlTarget).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}
	
	public static final <T> SqlQuery<T> create(SqlJoinTarget sqlJoin, Integer pageNum, Integer pageSize , Order... orders){
		return (SqlQuery<T>)new SqlQuery<T>().setTarget(sqlJoin).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}

	

}
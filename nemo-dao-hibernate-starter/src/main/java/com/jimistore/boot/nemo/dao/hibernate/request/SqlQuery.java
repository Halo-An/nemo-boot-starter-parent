package com.jimistore.boot.nemo.dao.hibernate.request;

import com.jimistore.boot.nemo.dao.api.request.ITarget;
import com.jimistore.boot.nemo.dao.api.request.Order;
import com.jimistore.boot.nemo.dao.api.request.Query;

public class SqlQuery<T> extends Query<T> {
	
	SqlTarget sqlTarget;

	@Override
	public ITarget getTarget() {
		return sqlTarget;
	}

	public SqlTarget getSqlTarget() {
		return sqlTarget;
	}

	public SqlQuery<T> setSqlTarget(SqlTarget sqlTarget) {
		this.sqlTarget = sqlTarget;
		return this;
	}

	@Override
	public SqlQuery<T> setTarget(ITarget target) {
		super.setTarget(target);
		return this;
	}
	
	public static final <T> SqlQuery<T> create(SqlTarget sqlTarget, Integer pageNum, Integer pageSize , Order... orders){
		return (SqlQuery<T>)new SqlQuery<T>().setSqlTarget(sqlTarget).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}

	

}
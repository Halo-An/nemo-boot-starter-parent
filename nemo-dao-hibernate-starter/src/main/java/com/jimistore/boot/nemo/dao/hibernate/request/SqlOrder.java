package com.jimistore.boot.nemo.dao.hibernate.request;

import java.io.Serializable;

import com.jimistore.boot.nemo.dao.api.enums.OrderType;
import com.jimistore.boot.nemo.dao.api.request.Order;

public class SqlOrder extends Order {
	
	SqlTarget sqlTarget;

	public SqlTarget getSqlTarget() {
		return sqlTarget;
	}

	public SqlOrder setSqlTarget(SqlTarget sqlTarget) {
		this.sqlTarget = sqlTarget;
		return this;
	}

	@Override
	public SqlOrder setOrderType(OrderType orderType) {
		super.setOrderType(orderType);
		return this;
	}

	@Override
	public SqlOrder setKey(Serializable key) {
		super.setKey(key);
		return this;
	}

	public static SqlOrder create(SqlTarget sqlTarget, Serializable key, OrderType orderType){
		return new SqlOrder().setSqlTarget(sqlTarget).setKey(key).setOrderType(orderType);
	}

}

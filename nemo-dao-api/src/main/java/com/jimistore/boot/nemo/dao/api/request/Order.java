package com.jimistore.boot.nemo.dao.api.request;

import com.jimistore.boot.nemo.dao.api.enums.OrderType;

public class Order {
	
	private OrderType orderType;
	
	private String key;
	
	private Order(){}
	
	public static Order create(String key, OrderType orderType){
		return new Order().setKey(key).setOrderType(orderType);
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public Order setOrderType(OrderType orderType) {
		this.orderType = orderType;
		return this;
	}

	public String getKey() {
		return key;
	}

	public Order setKey(String key) {
		this.key = key;
		return this;
	}

}

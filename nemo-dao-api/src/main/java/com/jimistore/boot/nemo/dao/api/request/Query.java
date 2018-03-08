package com.jimistore.boot.nemo.dao.api.request;

public class Query implements IQuery {
	
	Filter filter;
	
	private Integer pageNum;
	
	private Integer pageSize=12;
	
	Order[] orders;
	
	String[] columns;
	
	private Query(){}
	
	public static Query create(Filter filter, Order... orders){
		return new Query().setFilter(filter).setOrders(orders);
	}
	
	public static Query create(Filter filter, Integer pageNum, Integer pageSize , Order... orders){
		return new Query().setFilter(filter).setPageNum(pageNum).setPageSize(pageSize).setOrders(orders);
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public Query setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Query setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Order[] getOrders() {
		return orders;
	}

	public Query setOrders(Order... orders) {
		this.orders = orders;
		return this;
	}

	public String[] getColumns() {
		return columns;
	}

	public Query setColumns(String[] columns) {
		this.columns = columns;
		return this;
	}

	public Filter getFilter() {
		return filter;
	}

	public Query setFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

}

package com.jimistore.boot.nemo.dao.api.request;

public class Target implements ITarget {
	
	Class<?> entityClass;
	
	String[] outFieldNames;
	
	Filter filter;

	@Override
	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String[] getOutFieldNames() {
		return outFieldNames;
	}

	public Target setOutFieldNames(String[] outFieldNames) {
		this.outFieldNames = outFieldNames;
		return this;
	}

	public Target setEntityClass(Class<?> entityClass) {
		this.entityClass = entityClass;
		return this;
	}

	public Filter getFilter() {
		return filter;
	}

	public Target setFilter(Filter filter) {
		this.filter = filter;
		return this;
	}

}

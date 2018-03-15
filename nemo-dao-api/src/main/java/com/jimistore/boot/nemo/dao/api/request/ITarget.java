package com.jimistore.boot.nemo.dao.api.request;

public interface ITarget {
	
	public Class<?> getEntityClass();
	
	public ITarget setEntityClass(Class<?> entityClass);
	
	public String[] getOutFieldNames();
	
	public Filter getFilter();
	
	

}

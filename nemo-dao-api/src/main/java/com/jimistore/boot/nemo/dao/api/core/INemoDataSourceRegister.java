package com.jimistore.boot.nemo.dao.api.core;

public interface INemoDataSourceRegister {

	/**
	 * 数据源类型标识
	 * 
	 * @return
	 */
	public String getKey();

	/**
	 * 
	 * @return
	 */
	public Class<?> getNemoDataSourceFactoryClass();

}

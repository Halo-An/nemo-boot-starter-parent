package com.jimistore.boot.nemo.dao.hibernate.helper;

import com.jimistore.boot.nemo.dao.api.core.INemoDataSourceRegister;

public class C3P0DataSourceRegister implements INemoDataSourceRegister {

	public static final String KEY = "c3p0";

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public Class<?> getNemoDataSourceFactoryClass() {
		return C3P0DataSourceFactory.class;
	}

}

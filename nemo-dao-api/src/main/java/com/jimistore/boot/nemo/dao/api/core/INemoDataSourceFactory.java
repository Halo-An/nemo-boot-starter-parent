package com.jimistore.boot.nemo.dao.api.core;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;

import com.jimistore.boot.nemo.dao.api.config.NemoDataSourceProperties;

public interface INemoDataSourceFactory<T extends DataSource> extends FactoryBean<T> {

	/**
	 * 创建数据源
	 * 
	 * @param properties
	 * @return
	 */
	public INemoDataSourceFactory<T> setNemoDataSourceProperties(NemoDataSourceProperties properties);

}

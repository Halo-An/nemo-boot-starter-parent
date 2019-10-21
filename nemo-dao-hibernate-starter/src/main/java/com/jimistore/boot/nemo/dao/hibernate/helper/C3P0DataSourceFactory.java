package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.beans.PropertyVetoException;

import com.jimistore.boot.nemo.dao.api.config.NemoDataSourceProperties;
import com.jimistore.boot.nemo.dao.api.core.INemoDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3P0DataSourceFactory implements INemoDataSourceFactory<ComboPooledDataSource> {

	NemoDataSourceProperties nemoDataSourceProperties;

	@Override
	public ComboPooledDataSource getObject() throws Exception {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(nemoDataSourceProperties.getDriverClass());
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}

		dataSource.setJdbcUrl(nemoDataSourceProperties.getJdbcUrl());
		dataSource.setUser(nemoDataSourceProperties.getUser());
		dataSource.setPassword(nemoDataSourceProperties.getPassword());

		dataSource.setMinPoolSize(nemoDataSourceProperties.getMinPoolSize());
		dataSource.setMaxPoolSize(nemoDataSourceProperties.getMaxPoolSize());
		dataSource.setMaxIdleTime(nemoDataSourceProperties.getMaxIdleTime());
		dataSource.setAcquireIncrement(nemoDataSourceProperties.getAcquireIncrement());
		dataSource.setMaxStatements(nemoDataSourceProperties.getMaxStatements());
		dataSource.setInitialPoolSize(nemoDataSourceProperties.getInitialPoolSize());
		dataSource.setPreferredTestQuery(nemoDataSourceProperties.getPreferredTestQuery());
		dataSource.setIdleConnectionTestPeriod(nemoDataSourceProperties.getIdleConnectionTestPeriod());
		dataSource.setAcquireRetryAttempts(nemoDataSourceProperties.getAcquireRetryAttempts());
		dataSource.setBreakAfterAcquireFailure(nemoDataSourceProperties.getBreakAfterAcquireFailure());
		dataSource.setTestConnectionOnCheckout(nemoDataSourceProperties.getTestConnectionOnCheckout());

		return dataSource;
	}

	@Override
	public Class<?> getObjectType() {
		return ComboPooledDataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public INemoDataSourceFactory<ComboPooledDataSource> setNemoDataSourceProperties(
			NemoDataSourceProperties nemoDataSourceProperties) {
		this.nemoDataSourceProperties = nemoDataSourceProperties;
		return this;
	}
}

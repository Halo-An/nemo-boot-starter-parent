package com.jimistore.boot.nemo.dao.hibernate.helper;

import org.springframework.beans.factory.FactoryBean;

import com.jimistore.boot.nemo.dao.hibernate.config.DataSourceProperties;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@SuppressWarnings("rawtypes")
public class BaseDataSource implements FactoryBean  {
	
	DataSourceProperties dataSourceProperties;

	public void setDataSourceProperties(DataSourceProperties dataSourceProperties) {
		this.dataSourceProperties = dataSourceProperties;
	}

	@Override
	public Object getObject() throws Exception {
		ComboPooledDataSource dataSource=new ComboPooledDataSource();
		dataSource.setDriverClass(dataSourceProperties.getDriverClass());
		
		dataSource.setJdbcUrl(dataSourceProperties.getJdbcUrl());
		dataSource.setUser(dataSourceProperties.getUser());
		dataSource.setPassword(dataSourceProperties.getPassword());

		dataSource.setMinPoolSize(dataSourceProperties.getMinPoolSize());
		dataSource.setMaxPoolSize(dataSourceProperties.getMaxPoolSize());
		dataSource.setMaxIdleTime(dataSourceProperties.getMaxIdleTime());
		dataSource.setAcquireIncrement(dataSourceProperties.getAcquireIncrement());
		dataSource.setMaxStatements(dataSourceProperties.getMaxStatements());
		dataSource.setInitialPoolSize(dataSourceProperties.getInitialPoolSize());
		dataSource.setIdleConnectionTestPeriod(dataSourceProperties.getIdleConnectionTestPeriod());
		dataSource.setAcquireRetryAttempts(dataSourceProperties.getAcquireRetryAttempts());
		dataSource.setBreakAfterAcquireFailure(dataSourceProperties.getBreakAfterAcquireFailure());
		dataSource.setTestConnectionOnCheckout(dataSourceProperties.getTestConnectionOnCheckout());
		dataSource.setTestConnectionOnCheckin(dataSourceProperties.getTestConnectionOnCheckin());
		return dataSource;
	}

	@Override
	public Class getObjectType() {
		
		return ComboPooledDataSource.class;
	}

	@Override
	public boolean isSingleton() {
		
		return true;
	}
	
}

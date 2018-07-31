package com.jimistore.boot.nemo.dao.hibernate.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nemo")
public class MutilDataSourceProperties {
	
	public static final String DEFAULT_DATASOURCE = "default";
	
	private Map<String, DataSourceProperties> datasource = new HashMap<String, DataSourceProperties>();
	
	private Map<String, HibernateProperties> hibernate = new HashMap<String, HibernateProperties>();

	public Map<String, DataSourceProperties> getDatasource() {
		return datasource;
	}

	public MutilDataSourceProperties setDatasource(Map<String, DataSourceProperties> datasource) {
		this.datasource = datasource;
		return this;
	}

	public Map<String, HibernateProperties> getHibernate() {
		return hibernate;
	}

	public MutilDataSourceProperties setHibernate(Map<String, HibernateProperties> hibernate) {
		this.hibernate = hibernate;
		return this;
	}

}
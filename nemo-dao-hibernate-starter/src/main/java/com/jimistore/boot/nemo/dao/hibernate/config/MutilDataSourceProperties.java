package com.jimistore.boot.nemo.dao.hibernate.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.jimistore.boot.nemo.dao.api.config.NemoDataSourceProperties;

@ConfigurationProperties(prefix = "nemo")
public class MutilDataSourceProperties {

	public static final String DEFAULT_DATASOURCE = "default";

	public static final String DATASROUCE_KEY = "datasource-key";
	public static final String DATASROUCE_TYPE_C3P0 = "C3P0";
	public static final String DATASROUCE_TYPE_HICARICP = "HICARICP";

	public String datasourceType = DATASROUCE_TYPE_C3P0;

	private Map<String, NemoDataSourceProperties> datasource = new HashMap<String, NemoDataSourceProperties>();

	private Map<String, HibernateProperties> hibernate = new HashMap<String, HibernateProperties>();

	public Map<String, NemoDataSourceProperties> getDatasource() {
		return datasource;
	}

	public MutilDataSourceProperties setDatasource(Map<String, NemoDataSourceProperties> datasource) {
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

	public String getDatasourceType() {
		return datasourceType;
	}

	public MutilDataSourceProperties setDatasourceType(String datasourceType) {
		this.datasourceType = datasourceType;
		return this;
	}

}
package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.io.IOException;

import javax.sql.DataSource;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.jimistore.boot.nemo.dao.api.config.NemoDataSourceProperties;
import com.jimistore.boot.nemo.dao.hibernate.config.HibernateProperties;

public class BaseSessionFactory extends LocalSessionFactoryBean {

	String key;

	HibernateProperties hibernatePropertie;

	NemoDataSourceProperties dataSourcePropertie;

	DataSource dataSource;

//	NamingStrategy namingStrategy;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		super.setDataSource(dataSource);
	}

//	@Autowired
//	@Override
//	public void setNamingStrategy(NamingStrategy namingStrategy) {
//		this.namingStrategy = namingStrategy;
//		super.setNamingStrategy(namingStrategy);
//	}

	public DataSource getDataSource() {
		return dataSource;
	}

//	public NamingStrategy getNamingStrategy() {
//		return namingStrategy;
//	}

	public BaseSessionFactory() {
		super();
	}

	public void setHibernatePropertie(HibernateProperties hibernatePropertie) {
		this.hibernatePropertie = hibernatePropertie;

	}

	public void setDataSourcePropertie(NemoDataSourceProperties dataSourcePropertie) {
		this.dataSourcePropertie = dataSourcePropertie;
	}

	@Override
	public void afterPropertiesSet() throws IOException {

		super.setPackagesToScan(hibernatePropertie.getPackagesToScan());

		this.getHibernateProperties().setProperty(AvailableSettings.SHOW_SQL, hibernatePropertie.getShow_sql());
		this.getHibernateProperties()
				.setProperty(AvailableSettings.HBM2DDL_AUTO, hibernatePropertie.getHbm2ddl().getAuto());

		this.getHibernateProperties()
				.setProperty(AvailableSettings.QUERY_PLAN_CACHE_MAX_SIZE,
						hibernatePropertie.getQuery().getPlan_cache_max_size());
		this.getHibernateProperties()
				.setProperty(AvailableSettings.QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE,
						hibernatePropertie.getQuery().getPlan_parameter_metadata_max_size());

		String driverClass = dataSourcePropertie.getDriverClass();
		String characterEncoding = dataSourcePropertie.getCharacterEncoding();
		this.getHibernateProperties().setProperty("connection.characterEncoding", characterEncoding);

		try {
			if (driverClass.indexOf("mysql") >= 0) {
				this.getHibernateProperties()
						.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.MySQLDialect");
			} else if (driverClass.indexOf("oracle") >= 0) {
				this.getHibernateProperties()
						.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.OracleDialect");
			} else if (driverClass.indexOf("sqlserver") >= 0 || driverClass.indexOf("jtds") >= 0) {
				this.getHibernateProperties()
						.setProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.SQLServerDialect");
			}
		} catch (Exception e) {

		}

		String dialect = hibernatePropertie.getDialect();
		if (dialect != null && dialect.trim().length() > 0) {
			this.getHibernateProperties().setProperty(AvailableSettings.DIALECT, dialect);
		}

		super.afterPropertiesSet();
	}

	public String getKey() {
		return key;
	}

	public HibernateProperties getHibernatePropertie() {
		return hibernatePropertie;
	}

	public NemoDataSourceProperties getDataSourcePropertie() {
		return dataSourcePropertie;
	}

	public BaseSessionFactory setKey(String key) {
		this.key = key;
		return this;
	}
}

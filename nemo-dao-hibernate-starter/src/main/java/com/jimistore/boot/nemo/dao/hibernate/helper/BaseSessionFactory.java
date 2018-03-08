package com.jimistore.boot.nemo.dao.hibernate.helper;

import javax.sql.DataSource;

import org.hibernate.cfg.NamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

@SuppressWarnings("deprecation")
public class BaseSessionFactory extends LocalSessionFactoryBean {

	boolean setDialect = false;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Autowired
	@Override
	public void setNamingStrategy(NamingStrategy namingStrategy) {
		super.setNamingStrategy(namingStrategy);
	}

	public BaseSessionFactory() {
		super();
	}

	public void setHibernateProperties(HibernateProperties hibernateProperties) {
		super.setPackagesToScan(hibernateProperties.getPackagesToScan());

		this.getHibernateProperties().setProperty("hibernate.show_sql", hibernateProperties.getShow_sql());
		this.getHibernateProperties().setProperty("hibernate.hbm2ddl.auto", hibernateProperties.getHbm2ddl().getAuto());

		String dialect = hibernateProperties.getDialect();
		if (dialect != null && dialect.trim().length() > 0) {
			this.getHibernateProperties().setProperty("hibernate.dialect", dialect);
			setDialect = true;
		}

	}

	public void setDataSourceProperties(DataSourceProperties dataSourceProperties){
		String driverClass = dataSourceProperties.getDriverClass();
		String characterEncoding = dataSourceProperties.getCharacterEncoding();
		this.getHibernateProperties().setProperty("connection.characterEncoding", characterEncoding);
		if(!setDialect){
			if(driverClass.indexOf("mysql")>=0){
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			}else if(driverClass.indexOf("oracle")>=0){
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
			}else if(driverClass.indexOf("sqlserver")>=0||driverClass.indexOf("jtds")>=0){
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
			}
		}
	}
	
}

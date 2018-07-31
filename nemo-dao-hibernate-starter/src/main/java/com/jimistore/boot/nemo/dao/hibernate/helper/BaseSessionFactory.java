package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.io.IOException;

import javax.sql.DataSource;

import org.hibernate.cfg.NamingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import com.jimistore.boot.nemo.dao.hibernate.config.DataSourceProperties;
import com.jimistore.boot.nemo.dao.hibernate.config.HibernateProperties;

@SuppressWarnings("deprecation")
public class BaseSessionFactory extends LocalSessionFactoryBean {
	
	String key;
	
	HibernateProperties hibernatePropertie;
	
	DataSourceProperties dataSourcePropertie;

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

	public void setHibernatePropertie(HibernateProperties hibernatePropertie) {
		this.hibernatePropertie = hibernatePropertie;

	}

	public void setDataSourcePropertie(DataSourceProperties dataSourcePropertie){
		this.dataSourcePropertie = dataSourcePropertie;
	}

	@Override
	public void afterPropertiesSet() throws IOException {
		
		super.setPackagesToScan(hibernatePropertie.getPackagesToScan());

		this.getHibernateProperties().setProperty("hibernate.show_sql", hibernatePropertie.getShow_sql());
		this.getHibernateProperties().setProperty("hibernate.hbm2ddl.auto", hibernatePropertie.getHbm2ddl().getAuto());
		

		String driverClass = dataSourcePropertie.getDriverClass();
		String characterEncoding = dataSourcePropertie.getCharacterEncoding();
		this.getHibernateProperties().setProperty("connection.characterEncoding", characterEncoding);
		
		try{
			if(driverClass.indexOf("mysql")>=0){
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			}else if(driverClass.indexOf("oracle")>=0){
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.OracleDialect");
			}else if(driverClass.indexOf("sqlserver")>=0||driverClass.indexOf("jtds")>=0){
				this.getHibernateProperties().setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
			}
		}catch(Exception e){
			
		}
		

		String dialect = hibernatePropertie.getDialect();
		if (dialect != null && dialect.trim().length() > 0) {
			this.getHibernateProperties().setProperty("hibernate.dialect", dialect);
		}
		
		
		super.afterPropertiesSet();
	}

	public String getKey() {
		return key;
	}

	public BaseSessionFactory setKey(String key) {
		this.key = key;
		return this;
	}
}

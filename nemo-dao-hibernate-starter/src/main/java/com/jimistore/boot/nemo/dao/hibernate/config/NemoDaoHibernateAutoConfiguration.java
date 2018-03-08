package com.jimistore.boot.nemo.dao.hibernate.config;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import com.jimistore.boot.nemo.dao.api.dao.IDao;
import com.jimistore.boot.nemo.dao.api.validator.IQueryValidator;
import com.jimistore.boot.nemo.dao.api.validator.IXSSValidator;
import com.jimistore.boot.nemo.dao.hibernate.dao.HibernateDao;
import com.jimistore.boot.nemo.dao.hibernate.helper.BaseDataSource;
import com.jimistore.boot.nemo.dao.hibernate.helper.BaseSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.helper.DataSourceProperties;
import com.jimistore.boot.nemo.dao.hibernate.helper.HibernateNamingStrategy;
import com.jimistore.boot.nemo.dao.hibernate.helper.HibernateProperties;
import com.jimistore.boot.nemo.dao.hibernate.helper.IQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.QueryParser;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.InjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.XSSValidator;

@Configuration
@EnableConfigurationProperties({HibernateProperties.class, DataSourceProperties.class})
public class NemoDaoHibernateAutoConfiguration {

	HibernateProperties hibernateProperties;
	
	DataSourceProperties dataSourceProperties;
	
	public NemoDaoHibernateAutoConfiguration(HibernateProperties hibernateProperties,
			DataSourceProperties dataSourceProperties) {
		super();
		this.hibernateProperties = hibernateProperties;
		this.dataSourceProperties = dataSourceProperties;
	}

	@Bean
	@ConditionalOnMissingBean(HibernateNamingStrategy.class)
	public HibernateNamingStrategy hibernateNamingStrategy(){
		HibernateNamingStrategy hibernateNamingStrategy = new HibernateNamingStrategy();
		hibernateNamingStrategy.setHibernateProperties(hibernateProperties);
		return hibernateNamingStrategy;
	}
	
	@Bean
	@ConditionalOnMissingBean(BaseDataSource.class)
	public BaseDataSource BaseDataSource(){
		BaseDataSource baseDataSource = new BaseDataSource();
		baseDataSource.setDataSourceProperties(dataSourceProperties);
		return baseDataSource;
	}

	@Bean("sessionFactory")
	@ConditionalOnMissingBean(SessionFactory.class)
	public BaseSessionFactory baseSessionFactory(DataSource dataSource,HibernateNamingStrategy hibernateNamingStrategy){
		BaseSessionFactory sessionFactory = new BaseSessionFactory();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setNamingStrategy(hibernateNamingStrategy);
		sessionFactory.setDataSourceProperties(dataSourceProperties);
		sessionFactory.setHibernateProperties(hibernateProperties);
		return sessionFactory;
	}
	
	@Bean
	@ConditionalOnMissingBean(HibernateTransactionManager.class)
	public HibernateTransactionManager HibernateTransactionManager(SessionFactory sessionFactory){
		HibernateTransactionManager baseTransactionManager = new HibernateTransactionManager();
		baseTransactionManager.setSessionFactory(sessionFactory);
		return baseTransactionManager;
	}
	
	@Bean()
	@ConditionalOnMissingBean({IQueryParser.class, IQueryValidator.class})
	public IQueryParser IQueryParser(){
		return new QueryParser();
	}
	
	@Bean()
	@ConditionalOnMissingBean({IInjectSqlValidator.class})
	public IInjectSqlValidator IInjectSqlValidator(){
		return new InjectSqlValidator();
	}
	
	@Bean()
	@ConditionalOnMissingBean({IXSSValidator.class})
	public IXSSValidator IXSSValidator(){
		return new XSSValidator();
	}

	
	@Bean("hibernate")
	@ConditionalOnMissingBean(IDao.class)
	public HibernateDao HibernateDao(SessionFactory sessionFactory, IQueryParser queryParser, List<IQueryValidator> queryValidatorList){
		return new HibernateDao().setSessionFactory(sessionFactory).setQueryParser(queryParser).setQueryValidatorList(queryValidatorList);
	}
	
}

package com.jimistore.boot.nemo.dao.hibernate.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jimistore.boot.nemo.dao.api.dao.IDao;
import com.jimistore.boot.nemo.dao.api.validator.IQueryValidator;
import com.jimistore.boot.nemo.dao.api.validator.IXSSValidator;
import com.jimistore.boot.nemo.dao.hibernate.dao.HibernateDao;
import com.jimistore.boot.nemo.dao.hibernate.dao.MutilHibernateDao;
import com.jimistore.boot.nemo.dao.hibernate.helper.IQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilDaoAccessAspect;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactoryHelper;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.InjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.XSSValidator;

@Configuration
@EnableConfigurationProperties({HibernateProperties.class, DataSourceProperties.class, MutilDataSourceProperties.class})
@EnableTransactionManagement(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true", matchIfMissing = true)
public class NemoDaoHibernateAutoConfiguration {

	HibernateProperties hibernateProperties;
	
	DataSourceProperties dataSourceProperties;
	
	private MutilDataSourceProperties mutilDataSourceProperties;
	
	public NemoDaoHibernateAutoConfiguration(HibernateProperties hibernateProperties,
			DataSourceProperties dataSourceProperties, MutilDataSourceProperties mutilDataSourceProperties) {
		super();
		this.hibernateProperties = hibernateProperties;
		this.dataSourceProperties = dataSourceProperties;
		this.mutilDataSourceProperties = mutilDataSourceProperties;
		if(dataSourceProperties!=null&&dataSourceProperties.getJdbcUrl()!=null){
			this.mutilDataSourceProperties.getDatasource().put(MutilDataSourceProperties.DEFAULT_DATASOURCE, dataSourceProperties);
			this.mutilDataSourceProperties.getHibernate().put(MutilDataSourceProperties.DEFAULT_DATASOURCE, hibernateProperties);
		}
	}

//	@Bean
//	@ConditionalOnMissingBean(HibernateNamingStrategy.class)
//	public HibernateNamingStrategy HibernateNamingStrategy(){
//		HibernateNamingStrategy hibernateNamingStrategy = new HibernateNamingStrategy();
//		hibernateNamingStrategy.setHibernateProperties(hibernateProperties);
//		return hibernateNamingStrategy;
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean(BaseDataSource.class)
//	public BaseDataSource BaseDataSource(){
//		BaseDataSource baseDataSource = new BaseDataSource();
//		baseDataSource.setDataSourceProperties(dataSourceProperties);
//		return baseDataSource;
//	}
//
//	@Bean("sessionFactory")
//	@ConditionalOnMissingBean(SessionFactory.class)
//	public BaseSessionFactory BaseSessionFactory(DataSource dataSource,HibernateNamingStrategy hibernateNamingStrategy){
//		BaseSessionFactory sessionFactory = new BaseSessionFactory();
//		sessionFactory.setDataSource(dataSource);
//		sessionFactory.setNamingStrategy(hibernateNamingStrategy);
//		sessionFactory.setDataSourceProperties(dataSourceProperties);
//		sessionFactory.setHibernateProperties(hibernateProperties);
//		return sessionFactory;
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean(HibernateTransactionManager.class)
//	public HibernateTransactionManager HibernateTransactionManager(SessionFactory sessionFactory){
//		HibernateTransactionManager baseTransactionManager = new HibernateTransactionManager();
//		baseTransactionManager.setSessionFactory(sessionFactory);
//		return baseTransactionManager;
//	}
//	
//	@Bean()
//	@ConditionalOnMissingBean({IQueryParser.class, IQueryValidator.class})
//	public IQueryParser IQueryParser(HibernateNamingStrategy hibernateNamingStrategy){
//		return new QueryParser().setHibernateNamingStrategy(hibernateNamingStrategy);
//	}
//	
//	@Bean()
//	@ConditionalOnMissingBean({IInjectSqlValidator.class})
//	public IInjectSqlValidator IInjectSqlValidator(){
//		return new InjectSqlValidator();
//	}
//	
//	@Bean()
//	@ConditionalOnMissingBean({IXSSValidator.class})
//	public IXSSValidator IXSSValidator(){
//		return new XSSValidator();
//	}
//
//	
//	@Bean("hibernate")
//	@ConditionalOnMissingBean(IDao.class)
//	public HibernateDao HibernateDao(SessionFactory sessionFactory, IQueryParser queryParser, List<IXSSValidator> xssValidatorList, List<IInjectSqlValidator> queryValidatorList){
//		return new HibernateDao().setSessionFactory(sessionFactory).setQueryParser(queryParser).setXssValidatorList(xssValidatorList).setQueryValidatorList(queryValidatorList);
//	}
//	
//	@Bean
//	public HibernateDao hibernateDao(){
//		return new HibernateDao();
//	}
	
	@Bean
	@ConditionalOnMissingBean(MutilSessionFactory.class)
	public MutilSessionFactory mutilSessionFactory(){
		return new MutilSessionFactory().setMutilDataSourceProperties(mutilDataSourceProperties);
	}
	
	@Bean
	@ConditionalOnMissingBean(MutilSessionFactoryHelper.class)
	public MutilSessionFactoryHelper mutilSessionFactoryHelper(MutilSessionFactory mutilSessionFactory){
		return new MutilSessionFactoryHelper().setMutilSessionFactory(mutilSessionFactory);
	}
	
	@Bean
	@ConditionalOnMissingBean(MutilDaoAccessAspect.class)
	public MutilDaoAccessAspect daoAccessAspect(MutilSessionFactory mutilSessionFactory){
		return new MutilDaoAccessAspect().setMutilSessionFactory(mutilSessionFactory);
	}
	
	@Bean()
	@ConditionalOnMissingBean({IQueryParser.class, IQueryValidator.class})
	public IQueryParser IQueryParser(MutilSessionFactory mutilSessionFactory){
		return new MutilQueryParser().setMutilSessionFactory(mutilSessionFactory);
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
	public HibernateDao HibernateDao(MutilSessionFactory sessionFactory, IQueryParser queryParser, List<IXSSValidator> xssValidatorList, List<IInjectSqlValidator> queryValidatorList){
		return new MutilHibernateDao().setMutilSessionFactory(sessionFactory).setQueryParser(queryParser).setXssValidatorList(xssValidatorList).setQueryValidatorList(queryValidatorList);
	}
}

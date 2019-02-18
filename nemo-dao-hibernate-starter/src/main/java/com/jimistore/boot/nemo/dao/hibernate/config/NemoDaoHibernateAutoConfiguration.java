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
import com.jimistore.boot.nemo.dao.hibernate.dao.MutilHibernateQueryDao;
import com.jimistore.boot.nemo.dao.hibernate.helper.BaseSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.helper.IQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.ISpelExtendFunc;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilDaoAccessAspect;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilDataSourceHealthEndPoint;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactoryHelper;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilTransactionManager;
import com.jimistore.boot.nemo.dao.hibernate.helper.NotEmptySpelFunc;
import com.jimistore.boot.nemo.dao.hibernate.helper.QueryAspect;
import com.jimistore.boot.nemo.dao.hibernate.helper.QueryHelper;
import com.jimistore.boot.nemo.dao.hibernate.helper.SpelExtendFuncAspect;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.InjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.XSSValidator;

@Configuration
@EnableConfigurationProperties({ HibernateProperties.class, DataSourceProperties.class,
		MutilDataSourceProperties.class })
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
		if (dataSourceProperties != null && dataSourceProperties.getJdbcUrl() != null) {
			this.mutilDataSourceProperties.getDatasource().put(MutilDataSourceProperties.DEFAULT_DATASOURCE,
					dataSourceProperties);
			this.mutilDataSourceProperties.getHibernate().put(MutilDataSourceProperties.DEFAULT_DATASOURCE,
					hibernateProperties);
		}
	}

	@Bean
	@ConditionalOnMissingBean(MutilSessionFactory.class)
	public MutilSessionFactory mutilSessionFactory() {
		return new MutilSessionFactory().setMutilDataSourceProperties(mutilDataSourceProperties);
	}

	@Bean
	@ConditionalOnMissingBean(MutilTransactionManager.class)
	public MutilTransactionManager mutilTransactionManager() {
		return new MutilTransactionManager();
	}

	@Bean
	@ConditionalOnMissingBean(MutilSessionFactoryHelper.class)
	public MutilSessionFactoryHelper mutilSessionFactoryHelper(MutilSessionFactory mutilSessionFactory,
			MutilTransactionManager mutilTransactionManager) {
		return new MutilSessionFactoryHelper().setMutilSessionFactory(mutilSessionFactory)
				.setMutilTransactionManager(mutilTransactionManager);
	}

	@Bean("db")
	@ConditionalOnMissingBean(MutilDataSourceHealthEndPoint.class)
	public MutilDataSourceHealthEndPoint butilDataSourceHealthEndPoint(List<BaseSessionFactory> sessionFactoryList) {
		return new MutilDataSourceHealthEndPoint().setSessionFactoryList(sessionFactoryList);
	}

	@Bean
	@ConditionalOnMissingBean(MutilDaoAccessAspect.class)
	public MutilDaoAccessAspect daoAccessAspect(MutilSessionFactory mutilSessionFactory) {
		return new MutilDaoAccessAspect().setMutilSessionFactory(mutilSessionFactory);
	}

	@Bean()
	@ConditionalOnMissingBean({ IQueryParser.class, IQueryValidator.class })
	public IQueryParser IQueryParser(MutilSessionFactory mutilSessionFactory) {
		return new MutilQueryParser().setMutilSessionFactory(mutilSessionFactory);
	}

	@Bean()
	@ConditionalOnMissingBean({ IInjectSqlValidator.class })
	public IInjectSqlValidator IInjectSqlValidator() {
		return new InjectSqlValidator();
	}

	@Bean()
	@ConditionalOnMissingBean({ IXSSValidator.class })
	public IXSSValidator IXSSValidator() {
		return new XSSValidator();
	}

	@Bean("hibernate")
	@ConditionalOnMissingBean(IDao.class)
	public MutilHibernateQueryDao MutilHibernateQueryDao(MutilSessionFactory sessionFactory, IQueryParser queryParser,
			List<IXSSValidator> xssValidatorList, List<IInjectSqlValidator> queryValidatorList) {
		MutilHibernateQueryDao mutilHibernateQueryDao = new MutilHibernateQueryDao();
		mutilHibernateQueryDao.setMutilSessionFactory(sessionFactory).setQueryParser(queryParser)
				.setXssValidatorList(xssValidatorList).setQueryValidatorList(queryValidatorList);
		return mutilHibernateQueryDao;
	}

	@Bean()
	@ConditionalOnMissingBean(NotEmptySpelFunc.class)
	public NotEmptySpelFunc spelExtendFunc() {
		return new NotEmptySpelFunc();
	}

	@Bean()
	@ConditionalOnMissingBean(SpelExtendFuncAspect.class)
	public SpelExtendFuncAspect spelExtendFuncAspect(List<IInjectSqlValidator> queryValidatorList) {
		return new SpelExtendFuncAspect().setQueryValidatorList(queryValidatorList);
	}

	@Bean
	@ConditionalOnMissingBean(QueryHelper.class)
	public QueryHelper QueryHelper(MutilHibernateQueryDao mutilHibernateQueryDao,
			List<IInjectSqlValidator> queryValidatorList, List<ISpelExtendFunc> spelExtendFuncList) {
		return new QueryHelper().setQueryValidatorList(queryValidatorList)
				.setMutilHibernateQueryDao(mutilHibernateQueryDao).setSpelExtendFuncList(spelExtendFuncList);
	}

	@Bean
	@ConditionalOnMissingBean(QueryAspect.class)
	public QueryAspect QueryAspect(QueryHelper queryHelper) {
		return new QueryAspect().setQueryHelper(queryHelper);
	}
}

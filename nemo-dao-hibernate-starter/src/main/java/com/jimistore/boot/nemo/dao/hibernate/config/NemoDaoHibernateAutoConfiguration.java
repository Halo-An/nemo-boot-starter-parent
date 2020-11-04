package com.jimistore.boot.nemo.dao.hibernate.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jimistore.boot.nemo.dao.api.config.NemoDataSourceProperties;
import com.jimistore.boot.nemo.dao.api.core.INemoDataSourceRegister;
import com.jimistore.boot.nemo.dao.api.dao.IDao;
import com.jimistore.boot.nemo.dao.api.validator.IQueryValidator;
import com.jimistore.boot.nemo.dao.api.validator.IXSSValidator;
import com.jimistore.boot.nemo.dao.hibernate.dao.MutilHibernateQueryDao;
import com.jimistore.boot.nemo.dao.hibernate.helper.BaseSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.helper.C3P0DataSourceRegister;
import com.jimistore.boot.nemo.dao.hibernate.helper.DataSourceSelector;
import com.jimistore.boot.nemo.dao.hibernate.helper.IQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.ISpelExtendFunc;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilDaoAccessAspect;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilDataSourceHealthEndPoint;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactoryHelper;
import com.jimistore.boot.nemo.dao.hibernate.helper.NotEmptyArraySpelFunc;
import com.jimistore.boot.nemo.dao.hibernate.helper.NotEmptySpelFunc;
import com.jimistore.boot.nemo.dao.hibernate.helper.QueryAspect;
import com.jimistore.boot.nemo.dao.hibernate.helper.QueryHelper;
import com.jimistore.boot.nemo.dao.hibernate.helper.SpelExtendFuncAspect;
import com.jimistore.boot.nemo.dao.hibernate.helper.TransactionManagerFactory;
import com.jimistore.boot.nemo.dao.hibernate.qbatis.DaoExecutorHelper;
import com.jimistore.boot.nemo.dao.hibernate.qbatis.DaoExecutorSelect;
import com.jimistore.boot.nemo.dao.hibernate.qbatis.DaoProxyCreator;
import com.jimistore.boot.nemo.dao.hibernate.qbatis.IDaoExecutor;
import com.jimistore.boot.nemo.dao.hibernate.qbatis.IDaoSpelFunc;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.InjectSqlValidator;
import com.jimistore.boot.nemo.dao.hibernate.validator.XSSValidator;

@Configuration
@EnableConfigurationProperties({ HibernateProperties.class, NemoDataSourceProperties.class,
		MutilDataSourceProperties.class })
@EnableTransactionManagement(proxyTargetClass = true)
@ConditionalOnProperty(prefix = "spring.aop", name = "proxy-target-class", havingValue = "true", matchIfMissing = true)
public class NemoDaoHibernateAutoConfiguration {

	HibernateProperties hibernateProperties;

	NemoDataSourceProperties dataSourceProperties;

	private MutilDataSourceProperties mutilDataSourceProperties;

	public NemoDaoHibernateAutoConfiguration(HibernateProperties hibernateProperties,
			NemoDataSourceProperties dataSourceProperties, MutilDataSourceProperties mutilDataSourceProperties) {
		super();
		this.hibernateProperties = hibernateProperties;
		this.dataSourceProperties = dataSourceProperties;
		this.mutilDataSourceProperties = mutilDataSourceProperties;
		if (dataSourceProperties != null && dataSourceProperties.getJdbcUrl() != null) {
			this.mutilDataSourceProperties.getDatasource()
					.put(MutilDataSourceProperties.DEFAULT_DATASOURCE, dataSourceProperties);
			this.mutilDataSourceProperties.getHibernate()
					.put(MutilDataSourceProperties.DEFAULT_DATASOURCE, hibernateProperties);
		}
	}

	@Bean
	@ConditionalOnMissingBean(C3P0DataSourceRegister.class)
	public C3P0DataSourceRegister c3P0DataSourceRegister() {
		return new C3P0DataSourceRegister();
	}

	@Bean
	@ConditionalOnMissingBean(DataSourceSelector.class)
	public DataSourceSelector dataSourceSelector(@Lazy List<INemoDataSourceRegister> nemoDataSourceRegisterList) {
		return new DataSourceSelector().setNemoDataSourceRegisterList(nemoDataSourceRegisterList);
	}

	@Bean
	@ConditionalOnMissingBean(MutilSessionFactoryHelper.class)
	public MutilSessionFactoryHelper mutilSessionFactoryHelper(DataSourceSelector dataSourceSelector) {
		return new MutilSessionFactoryHelper().setMutilDataSourceProperties(mutilDataSourceProperties)
				.setDataSourceSelector(dataSourceSelector);
	}

	@Bean
	@ConditionalOnMissingBean(MutilSessionFactory.class)
	public MutilSessionFactory mutilSessionFactory(@Lazy List<BaseSessionFactory> sessionFactoryList) {
		return new MutilSessionFactory().setSessionFactoryList(sessionFactoryList);
	}

//	@Bean
//	@ConditionalOnMissingBean(MutilTransactionManager.class)
//	public MutilTransactionManager mutilTransactionManager(@Lazy List<BaseSessionFactory> sessionFactoryList) {
//		return new MutilTransactionManager().setSessionFactoryList(sessionFactoryList);
//	}

	@Bean
	@ConditionalOnMissingBean(PlatformTransactionManager.class)
	public TransactionManagerFactory transactionManagerFactory(@Lazy List<BaseSessionFactory> sessionFactoryList) {
		return new TransactionManagerFactory().setSessionFactoryList(sessionFactoryList);
	}

	@Bean("db")
	@ConditionalOnMissingBean(MutilDataSourceHealthEndPoint.class)
	public MutilDataSourceHealthEndPoint mutilDataSourceHealthEndPoint(
			@Lazy List<BaseSessionFactory> sessionFactoryList) {
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
		mutilHibernateQueryDao.setMutilSessionFactory(sessionFactory)
				.setQueryParser(queryParser)
				.setXssValidatorList(xssValidatorList)
				.setQueryValidatorList(queryValidatorList);
		return mutilHibernateQueryDao;
	}

	@Bean()
	@ConditionalOnMissingBean(NotEmptySpelFunc.class)
	public NotEmptySpelFunc spelExtendFunc() {
		return new NotEmptySpelFunc();
	}

	@Bean()
	@ConditionalOnMissingBean(NotEmptyArraySpelFunc.class)
	public NotEmptyArraySpelFunc notEmptyArraySpelFunc() {
		return new NotEmptyArraySpelFunc();
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
				.setMutilHibernateQueryDao(mutilHibernateQueryDao)
				.setSpelExtendFuncList(spelExtendFuncList);
	}

	@Bean
	@ConditionalOnMissingBean(QueryAspect.class)
	public QueryAspect QueryAspect(QueryHelper queryHelper) {
		return new QueryAspect().setQueryHelper(queryHelper);
	}

	@Bean
	@ConditionalOnMissingBean(DaoExecutorHelper.class)
	public DaoExecutorHelper daoExecutorHelper() {
		return new DaoExecutorHelper();
	}

	@Bean
	@ConditionalOnMissingBean(DaoExecutorSelect.class)
	public DaoExecutorSelect daoExecutorSelect(@Lazy MutilHibernateQueryDao mutilHibernateQueryDao,
			List<IDaoSpelFunc> daoSpelFuncList, DaoExecutorHelper daoExecutorHelper) {
		return new DaoExecutorSelect().setMutilHibernateQueryDao(mutilHibernateQueryDao)
				.setDaoSpelFuncList(daoSpelFuncList)
				.setDaoExecutorHelper(daoExecutorHelper);
	}

	@Bean
	@ConditionalOnMissingBean(DaoProxyCreator.class)
	public DaoProxyCreator daoProxyCreator(List<IDaoExecutor> daoExecutorList) {
		return new DaoProxyCreator().setDaoExecutorList(daoExecutorList);
	}

}

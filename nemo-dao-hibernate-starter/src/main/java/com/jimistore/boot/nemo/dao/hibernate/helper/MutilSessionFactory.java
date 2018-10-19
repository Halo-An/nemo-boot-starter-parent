package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.beans.PropertyVetoException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.apache.log4j.Logger;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import com.jimistore.boot.nemo.dao.hibernate.config.DataSourceProperties;
import com.jimistore.boot.nemo.dao.hibernate.config.HibernateProperties;
import com.jimistore.boot.nemo.dao.hibernate.config.MutilDataSourceProperties;
import com.mchange.v2.c3p0.ComboPooledDataSource;


@SuppressWarnings({ "deprecation", "serial" })
public class MutilSessionFactory implements ApplicationContextAware, InitializingBean, SessionFactory{
	
	Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();
	
	static Map<String, HibernateNamingStrategy> hibernateNamingStrategyMap = new HashMap<String, HibernateNamingStrategy>();
	
	static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
	
	private MutilDataSourceProperties mutilDataSourceProperties;
	
	ApplicationContext applicationContext;
	
	private static final Logger log = Logger.getLogger(MutilSessionFactory.class);
	
	
	public MutilSessionFactory(){}
	
	public MutilSessionFactory setMutilDataSourceProperties(MutilDataSourceProperties mutilDataSourceProperties) {
		this.mutilDataSourceProperties = mutilDataSourceProperties;
		return this;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
		
		Map<String, DataSourceProperties> dataSourceMap = mutilDataSourceProperties.getDatasource();
		Map<String, HibernateProperties> hibernateMap = mutilDataSourceProperties.getHibernate();
		for(Entry<String, DataSourceProperties> entry:dataSourceMap.entrySet()){
			
			DataSourceProperties dataSourceProperties = entry.getValue();
			HibernateProperties hibernateProperties = hibernateMap.get(entry.getKey());
			ComboPooledDataSource dataSource=new ComboPooledDataSource();
			try {
				dataSource.setDriverClass(dataSourceProperties.getDriverClass());
			} catch (PropertyVetoException e) {
				throw new RuntimeException(e);
			}
			
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
			
			HibernateNamingStrategy hibernateNamingStrategy = new HibernateNamingStrategy();
			hibernateNamingStrategy.setHibernateProperties(hibernateProperties);
			hibernateNamingStrategyMap.put(entry.getKey(), hibernateNamingStrategy);
			
			//注册sessionfactory
			String sessionFactoryName = String.format("MutilSessionFactory-%s", entry.getKey());
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
	                .rootBeanDefinition(BaseSessionFactory.class)
	                .addPropertyValue("key", entry.getKey())
	                .addPropertyValue("dataSource", dataSource)
	                .addPropertyValue("namingStrategy", hibernateNamingStrategy)
	                .addPropertyValue("hibernatePropertie", hibernateMap.get(entry.getKey()))
	                .addPropertyValue("dataSourcePropertie", entry.getValue());
			dlbf.registerBeanDefinition(sessionFactoryName, beanDefinitionBuilder.getBeanDefinition());
			
		}
		
		//注册事务
		String htmName = String.format("HibernateTransactionManager-%s", "proxy");
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(HibernateTransactionManager.class)
                .addPropertyValue("sessionFactory", this);
		dlbf.registerBeanDefinition(htmName, beanDefinitionBuilder.getBeanDefinition());
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;   
		//Bean的实例工厂  
		DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) context.getBeanFactory();
		this.postProcessBeanFactory(dbf);
	}
	
	public void put(String key, SessionFactory value){
		sessionFactoryMap.put(key, value);
	}
	
	
	public void setDataSourceKey(String key){
		threadLocal.set(key);
	}

	public SessionFactory getSessionFactory(){
		String key = threadLocal.get();
		if(log.isDebugEnabled()){
			log.debug(String.format("request getSessionFactory by datasource[%s]", key));
		}
		if(key==null){
			key = MutilDataSourceProperties.DEFAULT_DATASOURCE;
		}
		
		SessionFactory sessionFactory = sessionFactoryMap.get(key);
		if(sessionFactory==null){
			throw new RuntimeException(String.format("can not find datasource[%s] in configuration", key));
		}
		return sessionFactory;
	}
	
	public static HibernateNamingStrategy getHibernateNamingStrategy(){
		String key = threadLocal.get();
		if(key==null){
			key = MutilDataSourceProperties.DEFAULT_DATASOURCE;
		}
		HibernateNamingStrategy hibernateNamingStrategy = hibernateNamingStrategyMap.get(key);
		if(hibernateNamingStrategy==null){
			throw new RuntimeException(String.format("can not find datasource[%s] in configuration", key));
		}
		return hibernateNamingStrategy;
		
	}

	@Override
	public Reference getReference() throws NamingException {
		
		return this.getSessionFactory().getReference();
	}

	@Override
	public SessionFactoryOptions getSessionFactoryOptions() {
		
		return this.getSessionFactory().getSessionFactoryOptions();
	}

	@Override
	public SessionBuilder withOptions() {
		
		return this.getSessionFactory().withOptions();
	}

	@Override
	public Session openSession() throws HibernateException {
		
		return this.getSessionFactory().openSession();
	}

	@Override
	public Session getCurrentSession() throws HibernateException {
		
		return this.getSessionFactory().getCurrentSession();
	}

	@Override
	public StatelessSessionBuilder withStatelessOptions() {
		
		return this.getSessionFactory().withStatelessOptions();
	}

	@Override
	public StatelessSession openStatelessSession() {
		
		return this.getSessionFactory().openStatelessSession();
	}

	@Override
	public StatelessSession openStatelessSession(Connection connection) {
		
		return this.getSessionFactory().openStatelessSession(connection);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ClassMetadata getClassMetadata(Class entityClass) {
		
		return this.getSessionFactory().getClassMetadata(entityClass);
	}

	@Override
	public ClassMetadata getClassMetadata(String entityName) {
		
		return this.getSessionFactory().getClassMetadata(entityName);
	}

	@Override
	public CollectionMetadata getCollectionMetadata(String roleName) {
		
		return this.getSessionFactory().getCollectionMetadata(roleName);
	}

	@Override
	public Map<String, ClassMetadata> getAllClassMetadata() {
		
		return this.getSessionFactory().getAllClassMetadata();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map getAllCollectionMetadata() {
		
		return this.getSessionFactory().getAllCollectionMetadata();
	}

	@Override
	public Statistics getStatistics() {
		
		return this.getSessionFactory().getStatistics();
	}

	@Override
	public void close() throws HibernateException {
		
		this.getSessionFactory().close();
	}

	@Override
	public boolean isClosed() {
		
		return this.getSessionFactory().isClosed();
	}

	@Override
	public Cache getCache() {
		
		return this.getSessionFactory().getCache();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void evict(Class persistentClass) throws HibernateException {
		
		this.getSessionFactory().evict(persistentClass);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void evict(Class persistentClass, Serializable id) throws HibernateException {
		
		this.getSessionFactory().evict(persistentClass, id);
	}

	@Override
	public void evictEntity(String entityName) throws HibernateException {
		
		this.getSessionFactory().evictEntity(entityName);
	}

	@Override
	public void evictEntity(String entityName, Serializable id) throws HibernateException {
		
		this.getSessionFactory().evictEntity(entityName, id);
	}

	@Override
	public void evictCollection(String roleName) throws HibernateException {
		
		this.getSessionFactory().evictCollection(roleName);
	}

	@Override
	public void evictCollection(String roleName, Serializable id) throws HibernateException {
		
		this.getSessionFactory().evictCollection(roleName, id);
	}

	@Override
	public void evictQueries(String cacheRegion) throws HibernateException {
		
		this.getSessionFactory().evictQueries(cacheRegion);
	}

	@Override
	public void evictQueries() throws HibernateException {
		
		this.getSessionFactory().evictQueries();
	}

	@Override
	public Set<?> getDefinedFilterNames() {
		
		return this.getSessionFactory().getDefinedFilterNames();
	}

	@Override
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
		
		return this.getSessionFactory().getFilterDefinition(filterName);
	}

	@Override
	public boolean containsFetchProfileDefinition(String name) {
		
		return this.getSessionFactory().containsFetchProfileDefinition(name);
	}

	@Override
	public TypeHelper getTypeHelper() {
		
		return this.getSessionFactory().getTypeHelper();
	}

	

}

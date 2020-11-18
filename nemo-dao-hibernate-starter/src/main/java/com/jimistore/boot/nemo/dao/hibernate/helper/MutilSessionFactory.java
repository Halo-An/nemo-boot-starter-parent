package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;

import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.jimistore.boot.nemo.core.helper.Context;
import com.jimistore.boot.nemo.dao.hibernate.config.MutilDataSourceProperties;

@SuppressWarnings({ "deprecation", "serial", "rawtypes" })
public class MutilSessionFactory implements SessionFactory, InitializingBean {

	private static final Logger log = LoggerFactory.getLogger(MutilSessionFactory.class);

	Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();

	List<BaseSessionFactory> sessionFactoryList;

	public MutilSessionFactory setSessionFactoryList(List<BaseSessionFactory> sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
		return this;
	}

	public MutilSessionFactory() {
	}

	@Override
	public void afterPropertiesSet() {
		for (BaseSessionFactory sessionFactory : sessionFactoryList) {
			sessionFactoryMap.put(sessionFactory.getKey(), sessionFactory.getObject());
		}
	}

	private static String getDataSourceKey() {
		String key = (String) Context.get(MutilDataSourceProperties.DATASROUCE_KEY);
		if (key == null) {
			key = MutilDataSourceProperties.DEFAULT_DATASOURCE;
		}
		return key;
	}

	public SessionFactory getSessionFactoryProxy() {
		String key = getDataSourceKey();
		if (log.isDebugEnabled()) {
			log.debug(String.format("request getSessionFactory by datasource[%s]", key));
		}

		SessionFactory sessionFactory = sessionFactoryMap.get(key);
		if (sessionFactory == null) {
			throw new RuntimeException(String.format("can not find datasource[%s] in configuration", key));
		}
		return sessionFactory;
	}

	@Override
	public Reference getReference() throws NamingException {

		return this.getSessionFactoryProxy().getReference();
	}

	@Override
	public SessionBuilder withOptions() {

		return this.getSessionFactoryProxy().withOptions();
	}

	@Override
	public Session openSession() throws HibernateException {

		return this.getSessionFactoryProxy().openSession();
	}

	@Override
	public Session getCurrentSession() throws HibernateException {

		return this.getSessionFactoryProxy().getCurrentSession();
	}

	@Override
	public StatelessSessionBuilder withStatelessOptions() {

		return this.getSessionFactoryProxy().withStatelessOptions();
	}

	@Override
	public StatelessSession openStatelessSession() {

		return this.getSessionFactoryProxy().openStatelessSession();
	}

	@Override
	public StatelessSession openStatelessSession(Connection connection) {

		return this.getSessionFactoryProxy().openStatelessSession(connection);
	}

	@Override
	public ClassMetadata getClassMetadata(Class entityClass) {

		return this.getSessionFactoryProxy().getClassMetadata(entityClass);
	}

	@Override
	public ClassMetadata getClassMetadata(String entityName) {

		return this.getSessionFactoryProxy().getClassMetadata(entityName);
	}

	@Override
	public CollectionMetadata getCollectionMetadata(String roleName) {

		return this.getSessionFactoryProxy().getCollectionMetadata(roleName);
	}

	@Override
	public Map<String, ClassMetadata> getAllClassMetadata() {

		return this.getSessionFactoryProxy().getAllClassMetadata();
	}

	@Override
	public Map getAllCollectionMetadata() {

		return this.getSessionFactoryProxy().getAllCollectionMetadata();
	}

	@Override
	public Statistics getStatistics() {

		return this.getSessionFactoryProxy().getStatistics();
	}

	@Override
	public void close() throws HibernateException {

		this.getSessionFactoryProxy().close();
	}

	@Override
	public boolean isClosed() {

		return this.getSessionFactoryProxy().isClosed();
	}

	@Override
	public Cache getCache() {

		return this.getSessionFactoryProxy().getCache();
	}

	@Override
	public Set<?> getDefinedFilterNames() {

		return this.getSessionFactoryProxy().getDefinedFilterNames();
	}

	@Override
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {

		return this.getSessionFactoryProxy().getFilterDefinition(filterName);
	}

	@Override
	public boolean containsFetchProfileDefinition(String name) {

		return this.getSessionFactoryProxy().containsFetchProfileDefinition(name);
	}

	@Override
	public TypeHelper getTypeHelper() {

		return this.getSessionFactoryProxy().getTypeHelper();
	}

	@Override
	public EntityManager createEntityManager() {
		return this.getSessionFactoryProxy().createEntityManager();
	}

	@Override
	public EntityManager createEntityManager(Map map) {
		return this.getSessionFactoryProxy().createEntityManager(map);
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType) {
		return this.getSessionFactoryProxy().createEntityManager(synchronizationType);
	}

	@Override
	public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
		return this.getSessionFactoryProxy().createEntityManager(synchronizationType, map);
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return this.getSessionFactoryProxy().getCriteriaBuilder();
	}

	@Override
	public boolean isOpen() {
		return this.getSessionFactoryProxy().isOpen();
	}

	@Override
	public Map<String, Object> getProperties() {
		return this.getSessionFactoryProxy().getProperties();
	}

	@Override
	public PersistenceUnitUtil getPersistenceUnitUtil() {
		return this.getSessionFactoryProxy().getPersistenceUnitUtil();
	}

	@Override
	public void addNamedQuery(String name, Query query) {
		this.getSessionFactoryProxy().addNamedQuery(name, query);

	}

	@Override
	public <T> T unwrap(Class<T> cls) {
		return this.getSessionFactoryProxy().unwrap(cls);
	}

	@Override
	public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
		this.getSessionFactoryProxy().addNamedEntityGraph(graphName, entityGraph);
	}

	@Override
	public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
		return this.getSessionFactoryProxy().findEntityGraphsByType(entityClass);
	}

	@Override
	public Metamodel getMetamodel() {
		return this.getSessionFactoryProxy().getMetamodel();
	}

	@Override
	public SessionFactoryOptions getSessionFactoryOptions() {
		return this.getSessionFactoryProxy().getSessionFactoryOptions();
	}

}

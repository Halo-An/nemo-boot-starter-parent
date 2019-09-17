package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import com.jimistore.boot.nemo.core.helper.Context;
import com.jimistore.boot.nemo.dao.hibernate.config.MutilDataSourceProperties;

public class MutilTransactionManager extends HibernateTransactionManager implements InitializingBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Map<String, HibernateTransactionManager> hibernateTransactionManagerMap = new HashMap<String, HibernateTransactionManager>();

	BeanFactory beanFactory;

	List<BaseSessionFactory> sessionFactoryList;

	public MutilTransactionManager setSessionFactoryList(List<BaseSessionFactory> sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
		return this;
	}

	@Override
	public void afterPropertiesSet() {
		for (BaseSessionFactory sessionFactory : sessionFactoryList) {
			HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
			hibernateTransactionManager.setSessionFactory(sessionFactory.getObject());
			hibernateTransactionManager.setBeanFactory(beanFactory);
			hibernateTransactionManager.afterPropertiesSet();
			hibernateTransactionManagerMap.put(sessionFactory.getKey(), hibernateTransactionManager);

		}
	}

	protected HibernateTransactionManager getProxy() {
		String key = (String) Context.get(MutilDataSourceProperties.DATASROUCE_KEY);
		if (key == null) {
			key = MutilDataSourceProperties.DEFAULT_DATASOURCE;
		}
		return hibernateTransactionManagerMap.get(key);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		for (Entry<String, HibernateTransactionManager> entry : hibernateTransactionManagerMap.entrySet()) {
			entry.getValue().setBeanFactory(beanFactory);
		}
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {

		getProxy().setSessionFactory(sessionFactory);
	}

	@Override
	public SessionFactory getSessionFactory() {

		return getProxy().getSessionFactory();
	}

	@Override
	public void setDataSource(DataSource dataSource) {

		getProxy().setDataSource(dataSource);
	}

	@Override
	public DataSource getDataSource() {

		return getProxy().getDataSource();
	}

	@Override
	public void setAutodetectDataSource(boolean autodetectDataSource) {

		getProxy().setAutodetectDataSource(autodetectDataSource);
	}

	@Override
	public void setPrepareConnection(boolean prepareConnection) {

		getProxy().setPrepareConnection(prepareConnection);
	}

	@Override
	public void setAllowResultAccessAfterCompletion(boolean allowResultAccessAfterCompletion) {

		getProxy().setAllowResultAccessAfterCompletion(allowResultAccessAfterCompletion);
	}

	@Override
	public void setHibernateManagedSession(boolean hibernateManagedSession) {

		getProxy().setHibernateManagedSession(hibernateManagedSession);
	}

	@Override
	public void setEntityInterceptorBeanName(String entityInterceptorBeanName) {

		getProxy().setEntityInterceptorBeanName(entityInterceptorBeanName);
	}

	@Override
	public void setEntityInterceptor(Interceptor entityInterceptor) {

		getProxy().setEntityInterceptor(entityInterceptor);
	}

	@Override
	public Interceptor getEntityInterceptor() throws IllegalStateException, BeansException {

		return getProxy().getEntityInterceptor();
	}

	@Override
	public Object getResourceFactory() {

		return getProxy().getResourceFactory();
	}

}

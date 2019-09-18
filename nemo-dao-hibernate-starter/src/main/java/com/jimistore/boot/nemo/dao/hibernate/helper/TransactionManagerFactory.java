package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class TransactionManagerFactory implements FactoryBean<PlatformTransactionManager> {

	List<BaseSessionFactory> sessionFactoryList;

	public TransactionManagerFactory setSessionFactoryList(List<BaseSessionFactory> sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
		return this;
	}

	@Override
	public PlatformTransactionManager getObject() throws Exception {
		if (sessionFactoryList == null || sessionFactoryList.size() == 0) {
			throw new RuntimeException("sessionFactoryList cannot be empty");
		}
		if (sessionFactoryList.size() == 1) {
			BaseSessionFactory sessionFactory = sessionFactoryList.get(0);
			HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
			hibernateTransactionManager.setSessionFactory(sessionFactory.getObject());
			return hibernateTransactionManager;
		}

		HibernateTransactionManager[] hibernateTransactionManagers = new HibernateTransactionManager[sessionFactoryList
				.size()];
		for (int i = 0; i < sessionFactoryList.size(); i++) {
			BaseSessionFactory sessionFactory = sessionFactoryList.get(i);
			hibernateTransactionManagers[i] = new HibernateTransactionManager();
			hibernateTransactionManagers[i].setSessionFactory(sessionFactory.getObject());
			hibernateTransactionManagers[i].afterPropertiesSet();
		}

		return new ChainedTransactionManager(hibernateTransactionManagers);
	}

	@Override
	public Class<?> getObjectType() {
		return PlatformTransactionManager.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}

package com.jimistore.boot.nemo.dao.hibernate.helper;

public class MutilQueryParser extends QueryParser {

	MutilSessionFactory mutilSessionFactory;

	public MutilQueryParser setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	public HibernateNamingStrategy getHibernateNamingStrategy() {
		return MutilHibernateNamingStrategy.getHibernateNamingStrategy();
	}

}

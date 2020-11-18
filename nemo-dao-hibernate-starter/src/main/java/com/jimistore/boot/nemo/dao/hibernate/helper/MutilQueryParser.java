package com.jimistore.boot.nemo.dao.hibernate.helper;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy;

public class MutilQueryParser extends QueryParser {

	MutilSessionFactory mutilSessionFactory;

	public MutilQueryParser setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	public ImplicitNamingStrategy getImplicitNamingStrategy() {
		return MutilHibernateNamingStrategy.getNemoNamingStrategy();
	}

}

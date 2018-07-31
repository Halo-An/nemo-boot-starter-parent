package com.jimistore.boot.nemo.dao.hibernate.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.jimistore.boot.nemo.dao.api.validator.IXSSValidator;
import com.jimistore.boot.nemo.dao.hibernate.helper.IQueryParser;
import com.jimistore.boot.nemo.dao.hibernate.helper.MutilSessionFactory;
import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;

public class MutilHibernateDao extends HibernateDao {
	
	private MutilSessionFactory mutilSessionFactory;

	public MutilHibernateDao setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	public Session getSession(){
		return mutilSessionFactory.getSessionFactory().getCurrentSession();
	}

	@Override
	public MutilHibernateDao setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
		return this;
	}

	@Override
	public MutilHibernateDao setQueryParser(IQueryParser queryParser) {
		super.setQueryParser(queryParser);
		return this;
	}

	@Override
	public MutilHibernateDao setXssValidatorList(List<IXSSValidator> xssValidatorList) {
		super.setXssValidatorList(xssValidatorList);
		return this;
	}

	public MutilHibernateDao setQueryValidatorList(List<IInjectSqlValidator> queryValidatorList) {
		super.setQueryValidatorList(queryValidatorList);
		return this;
	}

}

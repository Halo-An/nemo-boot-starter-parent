package com.jimistore.boot.nemo.dao.hibernate.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;

public class MutilHibernateQueryDao extends MutilHibernateDao {
	
	public List<?> query(String hql, int pageNum, int pageSize){
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult((pageNum-1)*pageSize);
		query.setMaxResults(pageSize);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryBySql(String hql, int pageNum, int pageSize, Class<T> clazz){
		SQLQuery query = this.getSession().createSQLQuery(hql);
		query.setFirstResult((pageNum-1)*pageSize);
		query.setMaxResults(pageSize);
		if(clazz!=null && Map.class.isAssignableFrom(clazz)){
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		}
		return query.list();
	}

}

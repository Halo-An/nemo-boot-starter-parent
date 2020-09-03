package com.jimistore.boot.nemo.dao.hibernate.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimistore.boot.nemo.dao.hibernate.helper.AliasToEntityResultTransformer;

public class MutilHibernateQueryDao extends MutilHibernateDao {

	private static final Logger LOG = LoggerFactory.getLogger(MutilHibernateQueryDao.class);

	public List<?> query(String hql, int pageNum, int pageSize) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("request query, hql is \"%s\", pageNum is %s, pageSize is %s", hql, pageNum,
					pageSize));
		}
		Query query = this.getSession().createQuery(hql);
		query.setFirstResult((pageNum - 1) * pageSize);
		query.setMaxResults(pageSize);
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> queryBySql(String hql, int pageNum, int pageSize, Class<T> clazz) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("request queryBySql, sql is \"%s\", pageNum is %s, pageSize is %s", hql, pageNum,
					pageSize));
		}
		SQLQuery query = this.getSession().createSQLQuery(hql);
		query.setFirstResult((pageNum - 1) * pageSize);
		query.setMaxResults(pageSize);
		if (clazz != null) {

			if (Map.class.isAssignableFrom(clazz)) {
				query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			} else {
				query.setResultTransformer(AliasToEntityResultTransformer.create(clazz));
			}

		}
		return query.list();
	}

	public int execute(String sql) {
		return this.getSession().createSQLQuery(sql).executeUpdate();
	}

}

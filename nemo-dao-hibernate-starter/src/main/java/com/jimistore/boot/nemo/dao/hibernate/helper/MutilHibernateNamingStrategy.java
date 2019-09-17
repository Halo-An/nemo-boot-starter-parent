package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.HashMap;
import java.util.Map;

import com.jimistore.boot.nemo.core.helper.Context;
import com.jimistore.boot.nemo.dao.hibernate.config.MutilDataSourceProperties;

public class MutilHibernateNamingStrategy {

	static Map<String, HibernateNamingStrategy> hibernateNamingStrategyMap = new HashMap<String, HibernateNamingStrategy>();

	public static void put(String key, HibernateNamingStrategy hibernateNamingStrategy) {
		hibernateNamingStrategyMap.put(key, hibernateNamingStrategy);
	}

	private static String getDataSourceKey() {
		String key = (String) Context.get(MutilDataSourceProperties.DATASROUCE_KEY);
		if (key == null) {
			key = MutilDataSourceProperties.DEFAULT_DATASOURCE;
		}
		return key;
	}

	public static HibernateNamingStrategy getHibernateNamingStrategy() {
		String key = getDataSourceKey();
		HibernateNamingStrategy hibernateNamingStrategy = hibernateNamingStrategyMap.get(key);
		if (hibernateNamingStrategy == null) {
			throw new RuntimeException(String.format("can not find datasource[%s] in configuration", key));
		}
		return hibernateNamingStrategy;

	}

}

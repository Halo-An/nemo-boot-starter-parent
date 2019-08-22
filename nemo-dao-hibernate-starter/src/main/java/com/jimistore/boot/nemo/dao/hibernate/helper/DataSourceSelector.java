package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.jimistore.boot.nemo.dao.api.core.INemoDataSourceFactory;
import com.jimistore.boot.nemo.dao.api.core.INemoDataSourceRegister;

/**
 * 数据源匹配器
 * 
 * @author chenqi
 * @date 2019年7月12日
 *
 */
public class DataSourceSelector implements InitializingBean {

	Map<String, INemoDataSourceRegister> registerMap = new HashMap<String, INemoDataSourceRegister>();

	List<INemoDataSourceRegister> nemoDataSourceRegisterList;

	public DataSourceSelector setNemoDataSourceRegisterList(List<INemoDataSourceRegister> nemoDataSourceRegisterList) {
		this.nemoDataSourceRegisterList = nemoDataSourceRegisterList;
		return this;
	}

	public Class<?> getNemoDataSourceFactoryClass(String key) {
		if (key == null) {
			key = C3P0DataSourceRegister.KEY;
		}
		INemoDataSourceRegister register = registerMap.get(key);
		if (register == null) {
			throw new RuntimeException("can not find register[%s], please check properties of db");
		}
		Class<?> clazz = register.getNemoDataSourceFactoryClass();
		if (clazz == null) {
			throw new RuntimeException("nemo data source factory class can not be null");
		}
		if (!INemoDataSourceFactory.class.isAssignableFrom(clazz)) {
			throw new RuntimeException("nemo data source factory class must instanceof INemoDataSourceFactory");
		}
		return clazz;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		for (INemoDataSourceRegister register : nemoDataSourceRegisterList) {
			registerMap.put(register.getKey(), register);
		}
	}

}
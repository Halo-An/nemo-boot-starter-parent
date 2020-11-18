package com.jimistore.boot.nemo.rpc.eureka.server.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jimistore.boot.nemo.dao.api.dao.IDao;
import com.jimistore.boot.nemo.dao.api.enums.Compare;
import com.jimistore.boot.nemo.dao.api.request.Filter;
import com.jimistore.boot.nemo.dao.api.request.FilterEntry;
import com.jimistore.boot.nemo.dao.api.request.Query;
import com.jimistore.boot.nemo.rpc.eureka.server.entity.ServiceConfig;

@Transactional
@Repository
public class ServiceConfigDao {

	@Autowired
	IDao dao;

	public List<ServiceConfig> listAll() {
		return dao.list(
				Query.create(ServiceConfig.class, Filter.where(FilterEntry.create("1", Compare.eq, "1")), 1, 10000));

	}

}

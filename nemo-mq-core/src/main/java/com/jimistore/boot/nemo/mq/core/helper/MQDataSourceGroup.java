package com.jimistore.boot.nemo.mq.core.helper;

import java.util.List;

import com.jimistore.boot.nemo.mq.core.adapter.IMQDataSource;

public class MQDataSourceGroup {
	
	String type;
	
	List<IMQDataSource> dataSourceList;

	public String getType() {
		return type;
	}

	public MQDataSourceGroup setType(String type) {
		this.type = type;
		return this;
	}

	public List<IMQDataSource> getDataSourceList() {
		return dataSourceList;
	}

	public MQDataSourceGroup setDataSourceList(List<IMQDataSource> dataSourceList) {
		this.dataSourceList = dataSourceList;
		return this;
	}

}

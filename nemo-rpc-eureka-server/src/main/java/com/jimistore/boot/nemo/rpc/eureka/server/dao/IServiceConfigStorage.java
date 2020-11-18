package com.jimistore.boot.nemo.rpc.eureka.server.dao;

import com.jimistore.boot.nemo.rpc.eureka.server.entity.ServiceConfig;

/**
 * 监控报警白名单
 * 
 * @author chenqi
 * @date 2019年6月4日
 *
 */
public interface IServiceConfigStorage {

	public static final String SERVICE_DEFAULT_KEY = "default";

	/**
	 * 获取单个服务
	 * 
	 * @return
	 */
	public ServiceConfig get(String id);

}

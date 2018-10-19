package com.jimistore.boot.nemo.rpc.eureka.helper;

public interface IDynamicRpcServiceExporter {
	
	/**
	 * 根据服务名称获取一个service
	 * @param inf
	 * @param serviceName
	 * @param version
	 * @return
	 */
	public <T> T getRpcService(Class<T> inf, String serviceName, String version);

}

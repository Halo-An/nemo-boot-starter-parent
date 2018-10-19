package com.jimistore.boot.nemo.rpc.eureka.helper;

public interface INemoRpcClusterExporter {

	/**
	 * 从集群中读取接口地址
	 * @param moduleName
	 * @return
	 */
	public String getNextServerUrl(String moduleName);
	
}

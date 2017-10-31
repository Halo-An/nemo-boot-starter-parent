package com.jimistore.boot.nemo.rpc.eureka.helper;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

import com.cq.nemo.rpc.service.INemoRpcClusterExporter;

public class NemoRpcEurekaRibbonExporter implements INemoRpcClusterExporter {

    private LoadBalancerClient loadBalancerClient;

	public NemoRpcEurekaRibbonExporter setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
		this.loadBalancerClient = loadBalancerClient;
		return this;
	}

	@Override
	public String getNextServerUrl(String moduleName) {
		ServiceInstance server = loadBalancerClient.choose(moduleName);
		if(server==null){
			throw new RuntimeException(String.format("service not found in eureka : %s", moduleName));
		}
		return server.getUri().toString();
	}
	
	
	
}

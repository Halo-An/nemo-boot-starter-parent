package com.jimistore.boot.nemo.rpc.eureka.helper;

import com.jimistore.boot.nemo.core.api.service.OfflineHandler;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.discovery.DiscoveryManager;

@SuppressWarnings("deprecation")
public class EurekaOfflineHandler implements OfflineHandler {

	EurekaInstanceConfig eurekaInstanceConfig;

	public EurekaOfflineHandler setEurekaInstanceConfig(EurekaInstanceConfig eurekaInstanceConfig) {
		this.eurekaInstanceConfig = eurekaInstanceConfig;
		return this;
	}

	@Override
	public void offline() {
		DiscoveryManager.getInstance().shutdownComponent();
	}

}

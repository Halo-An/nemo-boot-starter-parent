package com.jimistore.boot.nemo.fuse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.fuse")
public class FuseProperties {
	
	int maxExecutorThreadSize = 100;

	public int getMaxExecutorThreadSize() {
		// TODO Auto-generated method stub
		return maxExecutorThreadSize;
	}

	public FuseProperties setMaxExecutorThreadSize(int maxExecutorThreadSize) {
		this.maxExecutorThreadSize = maxExecutorThreadSize;
		return this;
	}

}

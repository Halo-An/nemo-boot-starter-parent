package com.jimistore.boot.nemo.fuse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.fuse")
public class FuseProperties {

	int maxExecutorThreadSize = 100;

	double tryRatioThreshold = 0d;

	double openRatioThreshold = 0.5d;

	int openCountThreshold = 2;

	long checkInterval = 10000l;

	public int getMaxExecutorThreadSize() {
		// TODO Auto-generated method stub
		return maxExecutorThreadSize;
	}

	public FuseProperties setMaxExecutorThreadSize(int maxExecutorThreadSize) {
		this.maxExecutorThreadSize = maxExecutorThreadSize;
		return this;
	}

	public double getTryRatioThreshold() {
		return tryRatioThreshold;
	}

	public FuseProperties setTryRatioThreshold(double tryRatioThreshold) {
		this.tryRatioThreshold = tryRatioThreshold;
		return this;
	}

	public double getOpenRatioThreshold() {
		return openRatioThreshold;
	}

	public FuseProperties setOpenRatioThreshold(double openRatioThreshold) {
		this.openRatioThreshold = openRatioThreshold;
		return this;
	}

	public long getCheckInterval() {
		return checkInterval;
	}

	public FuseProperties setCheckInterval(long checkInterval) {
		this.checkInterval = checkInterval;
		return this;
	}

	public int getOpenCountThreshold() {
		return openCountThreshold;
	}

	public FuseProperties setOpenCountThreshold(int openCountThreshold) {
		this.openCountThreshold = openCountThreshold;
		return this;
	}
}

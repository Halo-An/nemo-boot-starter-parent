package com.jimistore.boot.nemo.fuse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.fuse")
public class FuseProperties {

	/**
	 * 最大线程数
	 */
	int maxExecutorThreadSize = 100;

	/**
	 * 熔断器重新尝试打调用异常比
	 */
	double tryRatioThreshold = 0d;

	/**
	 * 熔断器断开的调用异常比
	 */
	double openRatioThreshold = 0.5d;

	/**
	 * 熔断器断开调用量阈值(结合调用异常比一起使用)
	 */
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

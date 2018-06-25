package com.jimistore.boot.nemo.sliding.window.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.swindow")
public class SlidingWindowProperties {
	
	public static final String CACHE_MODEL_LOCAL = "local";
	
	public static final String CACHE_MODEL_REDIS = "redis";
		
	String cacheModel = CACHE_MODEL_LOCAL;
	
	/**
	 * 同步间隔
	 */
	int syncInterval = 30000;
	
	/**
	 * 失效清除计数器的计数容量
	 */
	int expiredCapacity = 60;
	
	/**
	 * 失效清除计数器倒数的偏移量
	 */
	int expiredOffset = 60;
	
	/**
	 * redis key的时效时间
	 */
	int redisExpired = 365 * 86400;

	public String getCacheModel() {
		return cacheModel;
	}

	public SlidingWindowProperties setCacheModel(String cacheModel) {
		this.cacheModel = cacheModel;
		return this;
	}

	public int getSyncInterval() {
		return syncInterval;
	}

	public SlidingWindowProperties setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
		return this;
	}

	public int getExpiredCapacity() {
		return expiredCapacity;
	}

	public SlidingWindowProperties setExpiredCapacity(int expiredCapacity) {
		this.expiredCapacity = expiredCapacity;
		return this;
	}

	public int getExpiredOffset() {
		return expiredOffset;
	}

	public SlidingWindowProperties setExpiredOffset(int expiredOffset) {
		this.expiredOffset = expiredOffset;
		return this;
	}

	public int getRedisExpired() {
		return redisExpired;
	}

	public SlidingWindowProperties setRedisExpired(int redisExpired) {
		this.redisExpired = redisExpired;
		return this;
	}
	
	
}

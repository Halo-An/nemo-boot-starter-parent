package com.jimistore.boot.nemo.sliding.window.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.swindow")
public class SlidingWindowProperties {
	
	public static final String CACHE_MODEL_LOCAL = "local";
	
	public static final String CACHE_MODEL_REDIS = "redis";
		
	String cacheModel = CACHE_MODEL_LOCAL;

	public String getCacheModel() {
		return cacheModel;
	}

	public SlidingWindowProperties setCacheModel(String cacheModel) {
		this.cacheModel = cacheModel;
		return this;
	}
	
	
}

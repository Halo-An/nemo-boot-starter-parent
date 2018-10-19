package com.jimistore.boot.nemo.rpc.eureka.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nemo.rpc")
public class NemoRpcProperties {
	
	/**
	 * 映射配置
	 */
	private Map<String, NemoRpcItem> map = new HashMap<String, NemoRpcItem>();
	
	/**
	 * 连接超时时间
	 */
	private int connectTimeOut = 3000;
	
	/**
	 * 数据读取超时时间
	 */
	private int readTimeOut = 10000;
	
	/**
	 * 是否忽略版本兼容
	 */
	private boolean ignoreVersionCompatible = true;

	public Map<String, NemoRpcItem> getMap() {
		return map;
	}

	public NemoRpcProperties setMap(Map<String, NemoRpcItem> map) {
		this.map = map;
		return this;
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public NemoRpcProperties setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
		return this;
	}

	public int getReadTimeOut() {
		return readTimeOut;
	}

	public NemoRpcProperties setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
		return this;
	}

	public boolean isIgnoreVersionCompatible() {
		return ignoreVersionCompatible;
	}

	public NemoRpcProperties setIgnoreVersionCompatible(boolean ignoreVersionCompatible) {
		this.ignoreVersionCompatible = ignoreVersionCompatible;
		return this;
	}

	
	
}

package com.jimistore.boot.nemo.sliding.window.core;

public class Publisher {
	
	String service;
	
	String key;

	public String getKey() {
		return key;
	}

	public Publisher setKey(String key) {
		this.key = key;
		return this;
	}

	public String getService() {
		return service;
	}

	public Publisher setService(String service) {
		this.service = service;
		return this;
	}
}

package com.jimistore.boot.nemo.sliding.window.core;

public class Topic {
	
	String publisherKey;
	
	String condition;
	
	String num;
	
	String key;
	
	int capacity;
	
	String timeUnit;
	
	String className;

	public String getKey() {
		return key;
	}

	public Topic setKey(String key) {
		this.key = key;
		return this;
	}

	public int getCapacity() {
		return capacity;
	}

	public Topic setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public String getTimeUnit() {
		return timeUnit;
	}

	public Topic setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public Topic setClassName(String className) {
		this.className = className;
		return this;
	}

	public String getPublisherKey() {
		return publisherKey;
	}

	public Topic setPublisherKey(String publisherKey) {
		this.publisherKey = publisherKey;
		return this;
	}

	public String getCondition() {
		return condition;
	}

	public Topic setCondition(String condition) {
		this.condition = condition;
		return this;
	}

	public String getNum() {
		return num;
	}

	public Topic setNum(String num) {
		this.num = num;
		return this;
	}

}

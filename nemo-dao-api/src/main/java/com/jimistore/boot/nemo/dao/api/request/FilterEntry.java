package com.jimistore.boot.nemo.dao.api.request;

import com.jimistore.boot.nemo.dao.api.enums.Compare;

public class FilterEntry {
	
	String key;
	
	Object value;
	
	Compare compare;
	
	private FilterEntry(){}
	
	public static FilterEntry create(String key, Compare compare, Object... value){
		Object obj = value;
		if(value.length==1){
			obj = value[0];
		}
		return new FilterEntry().setKey(key).setCompare(compare).setValue(obj);
	}

	public String getKey() {
		return key;
	}

	public FilterEntry setKey(String key) {
		this.key = key;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public FilterEntry setValue(Object value) {
		this.value = value;
		return this;
	}

	public Compare getCompare() {
		return compare;
	}

	public FilterEntry setCompare(Compare compare) {
		this.compare = compare;
		return this;
	}
}

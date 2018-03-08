package com.jimistore.boot.nemo.dao.api.enums;

public enum Compare{

	eq("=","等于"),
	lt("<","等于"),
	lte("<=","等于"),
	gt(">","等于"),
	gte(">=","等于"),
	in("in","在范围内"),
	nl("null","为空的"),
	nnl("notnull","不为空的"),
	like("like","模糊匹配");
	
	String code;
	
	String alias;

	private Compare(String code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	public String getCode() {
		return code;
	}

	public String getAlias() {
		return alias;
	}
	
	public Object formatValue(Object obj){
		return obj;
	}
	
}
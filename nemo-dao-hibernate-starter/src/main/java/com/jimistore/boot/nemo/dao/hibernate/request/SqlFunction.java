package com.jimistore.boot.nemo.dao.hibernate.request;

import java.io.Serializable;

public class SqlFunction implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4249623597990910176L;

	String content;
	
	Class<?> valueType;
	
	public static final SqlFunction create(String content, Class<?> valueType) {
		return new SqlFunction().setContent(content).setValueType(valueType);
	}

	public String getContent() {
		return content;
	}

	public SqlFunction setContent(String content) {
		this.content = content;
		return this;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public SqlFunction setValueType(Class<?> valueType) {
		this.valueType = valueType;
		return this;
	}
	
	

}

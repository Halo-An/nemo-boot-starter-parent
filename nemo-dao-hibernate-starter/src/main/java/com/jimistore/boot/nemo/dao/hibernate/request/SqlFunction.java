package com.jimistore.boot.nemo.dao.hibernate.request;

import java.io.Serializable;

import com.jimistore.boot.nemo.dao.api.annotation.XssIgnoreField;

public class SqlFunction implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4249623597990910176L;

	@XssIgnoreField()
	String content;
	
	String as;
	
	Class<?> valueType;
	
	public static final SqlFunction create(String content, Class<?> valueType) {
		return new SqlFunction().setContent(content).setValueType(valueType);
	}
	
	public static final SqlFunction create(String content, Class<?> valueType, String as) {
		return new SqlFunction().setContent(content).setValueType(valueType).setAs(as);
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

	public String getAs() {
		return as;
	}

	public SqlFunction setAs(String as) {
		this.as = as;
		return this;
	}
	
	

}

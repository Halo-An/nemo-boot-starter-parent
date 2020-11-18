package com.jimistore.boot.nemo.rpc.eureka.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.jimistore.boot.nemo.dao.api.entity.BaseBean;

@Entity
public class ServiceConfig extends BaseBean<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3998523956745187678L;

	@Id
	@Column(length = 32)
	String id;

	boolean disabled;

	@Column(length = 200)
	String robot;

	@Transient
	String[] phones;

	@Column(length = 100)
	String phoneStr;

	public String getId() {
		return id;
	}

	public ServiceConfig setId(String id) {
		this.id = id;
		return this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public ServiceConfig setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public String[] getPhones() {
		if (phoneStr != null) {
			return phoneStr.split(",");
		}
		return null;
	}

	public ServiceConfig setPhones(String[] phones) {
		if (phones != null && phones.length > 0) {
			this.phoneStr = String.join(",", phones);
		}
		return this;
	}

	public String getRobot() {
		return robot;
	}

	public ServiceConfig setRobot(String robot) {
		this.robot = robot;
		return this;
	}

	public String getPhoneStr() {
		return phoneStr;
	}

	public ServiceConfig setPhoneStr(String phoneStr) {
		this.phoneStr = phoneStr;
		return this;
	}

}

package com.jimistore.boot.nemo.fuse.core;

import com.jimistore.boot.nemo.fuse.enums.FuseState;

/**
 * 熔断器信息
 * @author chenqi
 * @date 2019年1月30日
 *
 */
public class FuseInfo implements IFuseInfo {
	
	String key;
	
	int hourlyRequestVolume;
	
	int hourlyTimeoutVolume;
	
	int hourlyExceptionVolume;
	
	FuseState fuseState;

	public String getKey() {
		return key;
	}

	public FuseInfo setKey(String key) {
		this.key = key;
		return this;
	}

	public int getHourlyRequestVolume() {
		return hourlyRequestVolume;
	}

	public FuseInfo setHourlyRequestVolume(int hourlyRequestVolume) {
		this.hourlyRequestVolume = hourlyRequestVolume;
		return this;
	}

	public int getHourlyTimeoutVolume() {
		return hourlyTimeoutVolume;
	}

	public FuseInfo setHourlyTimeoutVolume(int hourlyTimeoutVolume) {
		this.hourlyTimeoutVolume = hourlyTimeoutVolume;
		return this;
	}

	public int getHourlyExceptionVolume() {
		return hourlyExceptionVolume;
	}

	public FuseInfo setHourlyExceptionVolume(int hourlyExceptionVolume) {
		this.hourlyExceptionVolume = hourlyExceptionVolume;
		return this;
	}

	public FuseState getFuseState() {
		return fuseState;
	}

	public FuseInfo setFuseState(FuseState fuseState) {
		this.fuseState = fuseState;
		return this;
	}


}

package com.jimistore.boot.nemo.fuse.enums;

/**
 * 熔断器状态
 * 
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public enum FuseState {

	OPEN("DOWN"),
	CONNECT("UP", true),
	TRY("UNKNOWN"),
	TRYING("UNKNOWN");

	boolean available = false;

	String alias;

	private FuseState(String alias, boolean available) {
		this.available = available;
		this.alias = alias;
	}

	private FuseState(String alias) {
		this.alias = alias;
	}

	public boolean isAvailable() {
		return available;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public String toString() {
		return this.getAlias();
	}

}

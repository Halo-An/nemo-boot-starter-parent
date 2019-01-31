package com.jimistore.boot.nemo.fuse.enums;

/**
 * 熔断器状态
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public enum FuseState {
	
	OPEN,
	CONNECT(true),
	TRY(true),
	TRYING;
	
	boolean available = false;

	private FuseState(boolean available) {
		this.available = available;
	}

	private FuseState() {
	}

	public boolean isAvailable() {
		return available;
	}

}

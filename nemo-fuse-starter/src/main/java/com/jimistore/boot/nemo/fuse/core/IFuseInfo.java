package com.jimistore.boot.nemo.fuse.core;

import com.jimistore.boot.nemo.fuse.enums.FuseState;

public interface IFuseInfo {
	
	/**
	 * 熔断器标识
	 * @return
	 */
	public String getKey();
	
//	/**
//	 * 1小时调用量
//	 * @return
//	 */
//	public int getHourlyRequestVolume();
//	
//	/**
//	 * 1小时超时量
//	 * @return
//	 */
//	public int getHourlyTimeoutVolume();
//	
//	/**
//	 * 1小时异常量（含超时量）
//	 * @return
//	 */
//	public int getHourlyExceptionVolume();
	
	/**
	 * 熔断器状态
	 * @return
	 */
	public FuseState getFuseState();
	
	

}
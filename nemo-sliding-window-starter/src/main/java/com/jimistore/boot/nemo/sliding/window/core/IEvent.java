package com.jimistore.boot.nemo.sliding.window.core;

public interface IEvent<T extends Number> {
	
	/**
	 * 获取事件标识
	 * @return
	 */
	public String getTopicKey();
	
	/**
	 * 获取事件发生时间
	 * @return
	 */
	public Long getTime();

}

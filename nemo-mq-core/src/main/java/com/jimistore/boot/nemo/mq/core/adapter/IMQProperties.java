package com.jimistore.boot.nemo.mq.core.adapter;

public interface IMQProperties {
	
	/**
	 * 获取MQ数据源类型
	 * @return
	 */
	public String getType();
	
	/**
	 * 获取MQ数据源标识
	 * @return
	 */
	public String getKey();

}

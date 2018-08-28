package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;

public interface IChannelContainer {
	
	/**
	 * 放入发布的主题
	 * @param key
	 * @return
	 */
	public IChannelContainer put(String key);
	
	/**
	 * 放入发布的主题
	 * @param key
	 * @return
	 */
	public IChannelContainer delete(String key);
	
	/**
	 * 放入订阅的订阅者
	 * @param subscribe 订阅者
	 */
	public IChannelContainer put(ISubscriber subscriber);
	
	/**
	 * 获取匹配的通道
	 * @param key
	 * @return
	 */
	public List<IChannel> match(String key);

}

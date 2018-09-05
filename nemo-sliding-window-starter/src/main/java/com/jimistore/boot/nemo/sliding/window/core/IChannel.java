package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;

public interface IChannel {
	
	/**
	 * 获取订阅的标识
	 * @return
	 */
	public List<String> getTopicList();
	
	/**
	 * 获取订阅的主题
	 * @return
	 */
	public ISubscriber getSubscriber();
	
	/**
	 * 准备(成功为true,失败false)
	 * @return
	 */
	public boolean ready();

}

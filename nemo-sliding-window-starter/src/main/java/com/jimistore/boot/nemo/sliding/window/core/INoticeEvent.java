package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;

public interface INoticeEvent<T extends Number> extends IEvent<T> {
	
	/**
	 * 获取事件值
	 * @return
	 */
	public List<T> getValue();
	
	/**
	 * 获取事件发生的订阅元数据
	 * @return
	 */
	public ISubscriber getSubscriber();

}

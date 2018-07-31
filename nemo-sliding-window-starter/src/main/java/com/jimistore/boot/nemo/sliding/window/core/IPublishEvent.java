package com.jimistore.boot.nemo.sliding.window.core;

public interface IPublishEvent<T extends Number> extends IEvent<T> {
	
	/**
	 * 获取事件值
	 * @return
	 */
	public T getValue();

}

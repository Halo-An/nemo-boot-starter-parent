package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ICounter<T> {
	
	/**
	 * 发布数据
	 * @param event
	 * @return
	 */
	public ICounter<T> put(IPublishEvent<?> event);
	
	/**
	 * 
	 * @param timeUnit 窗口长度单位
	 * @param length 窗口长度
	 * @param valueType 数据类型
	 */
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType);
	
}

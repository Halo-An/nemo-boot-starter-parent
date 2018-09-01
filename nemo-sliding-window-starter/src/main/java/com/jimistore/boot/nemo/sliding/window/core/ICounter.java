package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ICounter<T> {
	
	/**
	 * 获取计数器的标识
	 * @return
	 */
	public String getKey();
	
	/**
	 * 发布数据
	 * @param event
	 * @return
	 */
	public ICounter<T> put(IPublishEvent<?> event);
	
	/**
	 * 获取最新窗口的数据
	 * @param timeUnit 窗口长度单位
	 * @param length 窗口长度
	 * @param valueType 数据类型
	 */
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType);
	
	/**
	 * 根据指定时间获取窗口的数据
	 * @param timeUnit 窗口长度单位
	 * @param length 窗口长度
	 * @param valueType 数据类型
	 * @param timestamp 时间戳(ms)
	 */
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType, long timestamp);

	/**
	 * 获取计数器可计算的所有窗口的数据
	 * @param timeUnit 窗口长度单位
	 * @param length 窗口长度
	 * @param valueType 数据类型
	 */
	public <E> List<List<E>> listWindow(TimeUnit timeUnit, Integer length, Class<E> valueType);

	/**
	 * 获取计数器可计算的所有窗口的数据
	 * @param timeUnit 窗口长度单位
	 * @param length 窗口长度
	 * @param valueType 数据类型
	 */
	public <E> List<List<E>> listWindow(TimeUnit timeUnit, Integer length, Class<E> valueType, long timestamp);
	
	/**
	 * 计数心跳
	 */
	public void heartbeat();
	
}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface ICounterContainer {
	
	/**
	 * 创建计数器
	 * @param key 容器名称
	 * @param timeUnit 时间单位
	 * @param capacity 容量
	 * @param 数据类型
	 * @return
	 */
	public ICounterContainer createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType);
	
	/**
	 * 删除计数器
	 * @param key 容器名称
	 * @param timeUnit 时间单位
	 * @param capacity 容量
	 * @param 数据类型
	 * @return
	 */
	public ICounterContainer deleteCounter(String key);
	
	/**
	 * 发布数据
	 * @param event
	 * @return
	 */
	public ICounterContainer put(IPublishEvent<?> event);
	
	/**
	 * 获取所有key
	 * @return
	 */
	public Set<String> getAllKeys();
	
	/**
	 * 
	 * @param key
	 * @param timeUnit
	 * @param length
	 */
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType);
	
	/**
	 * 计数心跳
	 */
	public void heartbeat();

	/**
	 * 
	 * @param key
	 * @param timeUnit
	 * @param length
	 * @param valueType
	 * @return
	 */
	public <E> List<List<E>> listWindow(String key, TimeUnit timeUnit, Integer length, Class<E> valueType);

	
}

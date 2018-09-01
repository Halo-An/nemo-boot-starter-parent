package com.jimistore.boot.nemo.sliding.window.core;

import java.util.concurrent.TimeUnit;

import com.jimistore.boot.nemo.sliding.window.handler.INoticeHandler;
import com.jimistore.boot.nemo.sliding.window.handler.IPublishHandler;

public interface IDispatcher {
	
	/**
	 * 删除topic
	 * @param topicKey
	 * @return 
	 */
	public IDispatcher deleteTopic(String topicKey);
	
	/**
	 * 发布订阅
	 * @param subscriber
	 */
	public IDispatcher subscribe(ISubscriber subscriber);
	
	/**
	 * 发布计数
	 * @param event
	 */
	public IDispatcher publish(IPublishEvent<?> event);
	
	/**
	 * 添加发布处理器
	 * @param publishHandler
	 */
	public IDispatcher addPublishHandler(IPublishHandler publishHandler);
	
	/**
	 * 添加通知处理器
	 * @param noticeHandler
	 */
	public IDispatcher addNoticeHandler(INoticeHandler noticeHandler);
	
	/**
	 * 创建计数器
	 * @param key 容器名称
	 * @param timeUnit 时间单位
	 * @param capacity 容量
	 * @param 数据类型
	 * @return
	 */
	public IDispatcher createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType);

	/**
	 * 删除计数器
	 * @param key
	 * @return
	 */
	public IDispatcher deleteCounter(String key);

}

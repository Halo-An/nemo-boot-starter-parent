package com.jimistore.boot.nemo.sliding.window.core;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jimistore.boot.nemo.sliding.window.handler.INoticeHandler;
import com.jimistore.boot.nemo.sliding.window.handler.IPublishHandler;

public interface IDispatcher {
	
	/**
	 * 创建监控点
	 * @param publisher
	 * @return
	 */
	public IDispatcher createPublisher(Publisher publisher);
	
	/**
	 * 取一个窗口的数据
	 * @param key
	 * @param timeUnit
	 * @param length
	 */
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType);
	

	
	/**
	 * 
	 * @param key
	 * @param timeUnit
	 * @param length
	 */
	public <E> List<List<E>> listWindow(String key, TimeUnit timeUnit, Integer length, Class<E> valueType);
	
	
	/**
	 * 获取监控点集合
	 * @return 
	 */
	public Collection<Publisher> listPublisher();
	
	
	/**
	 * 删除监控点
	 * @param publisherKey
	 * @return 
	 */
	public IDispatcher deletePublisher(String publisherKey);
	
	
	/**
	 * 创建topic
	 * @param topic
	 * @return 
	 */
	public IDispatcher createTopic(Topic topic);
	
	
	/**
	 * 获取topic集合
	 * @param publisherKey
	 * @return 
	 */
	public Collection<Topic> listTopic();
	
	
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

}

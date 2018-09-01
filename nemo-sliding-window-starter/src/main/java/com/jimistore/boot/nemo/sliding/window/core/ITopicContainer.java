package com.jimistore.boot.nemo.sliding.window.core;

import java.util.Collection;

public interface ITopicContainer {
	
	/**
	 * 获取topic集合
	 * @param method
	 * @param targetClass
	 * @return
	 */
	public Collection<Topic> listTopic();
	
	/**
	 * 创建一个topic
	 * @param topic
	 */
	public ITopicContainer createTopic(Topic topic);
	
	/**
	 * 删除一个topic
	 * @param topicKey
	 */
	public ITopicContainer deleteTopic(String topicKey);
	
	

}
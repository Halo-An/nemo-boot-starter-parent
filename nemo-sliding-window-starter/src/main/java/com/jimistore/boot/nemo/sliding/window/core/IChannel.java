package com.jimistore.boot.nemo.sliding.window.core;

public interface IChannel {
	
	/**
	 * 获取订阅的标识
	 * @return
	 */
	public String getTopicKey();
	
	/**
	 * 设置订阅的标识
	 * @param topicKey
	 * @return
	 */
	public IChannel setTopicKey(String topicKey);
	
	public Long getNextTime();
	
	public IChannel setNextTime(Long time);
	
	public ISubscriber getSubscriber();
	
	public IChannel setSubscriber(ISubscriber subscriber);

}

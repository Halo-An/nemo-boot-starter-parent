package com.jimistore.boot.nemo.sliding.window.core;

public interface IPublisher {
	
	/**
	 * 发布计数
	 * @param counterEvent
	 */
	public void publish(IPublishEvent<?> counterEvent);

}

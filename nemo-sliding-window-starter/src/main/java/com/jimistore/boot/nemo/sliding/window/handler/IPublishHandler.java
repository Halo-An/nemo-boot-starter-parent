package com.jimistore.boot.nemo.sliding.window.handler;

import com.jimistore.boot.nemo.sliding.window.core.IPublishEvent;

public interface IPublishHandler {
	
	/**
	 * 发布之前的处理
	 * @param event
	 */
	public void before(IPublishEvent<?> event);
	
	/**
	 * 发布之后的处理
	 * @param event
	 */
	public void after(IPublishEvent<?> event);

}

package com.jimistore.boot.nemo.sliding.window.handler;

import com.jimistore.boot.nemo.sliding.window.core.INoticeEvent;

public interface INoticeHandler {
	
	/**
	 * 通知之前的处理
	 * @param event
	 */
	public void before(INoticeEvent<?> event);
	
	/**
	 * 通知之后的处理
	 * @param event
	 */
	public void after(INoticeEvent<?> event);

}

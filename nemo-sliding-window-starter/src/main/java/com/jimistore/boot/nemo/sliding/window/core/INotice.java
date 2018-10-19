package com.jimistore.boot.nemo.sliding.window.core;

public interface INotice {
	
	/**
	 * 通知
	 * @param event
	 */
	public void notice(INoticeEvent<?> event);

}

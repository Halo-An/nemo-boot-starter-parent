package com.jimistore.boot.nemo.sliding.window.core;

public interface IWarnSubscriber extends ISubscriber,IWarn {
	
	/**
	 * 是否只在告警时推送
	 * @return
	 */
	public default boolean isOnlyNoticeWarn(){
		return true;
	}

}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.concurrent.TimeUnit;

public interface ISubscriber {
	
	/**
	 * 订阅通知
	 * @param event
	 */
	public void notice(INoticeEvent<?> event);
	
	/**
	 * 订阅主题的通配符
	 * @return
	 */
	public String getTopicMatch();
	
	/**
	 * 订阅间隔和长度单位
	 * @return
	 */
	public default TimeUnit getTimeUnit(){
		return TimeUnit.SECONDS;
	}
	
	public default Class<?> getValueType(){
		return Integer.class;
	}
	
	/**
	 * 订阅窗口长度
	 * @return
	 */
	public Integer getLength();
	
	/**
	 * 订阅间隔
	 * @return
	 */
	public default Integer getInterval(){
		return 0;
	}
	

}

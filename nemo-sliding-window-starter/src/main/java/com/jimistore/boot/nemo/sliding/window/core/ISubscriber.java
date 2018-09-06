package com.jimistore.boot.nemo.sliding.window.core;

import java.util.concurrent.TimeUnit;

public interface ISubscriber {
	
	/**
	 * 获取通知回调
	 * @return
	 */
	public INotice getNotice();
	
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
	
	/**
	 * 值类型
	 * @return
	 */
	public default Class<?> getValueType(){
		return Integer.class;
	}
	
	/**
	 * 订阅窗口长度
	 * @return
	 */
	public Integer getLength();
	
	/**
	 * 订阅间隔(单位为毫秒)
	 * @return
	 */
	public default Long getInterval(){
		return 0l;
	}
	
	/**
	 * 订阅开始时间(单位为毫秒)
	 * @return
	 */
	public default Long getStart(){
		return 0l;
	}
	

}

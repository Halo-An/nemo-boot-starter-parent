package com.jimistore.boot.nemo.mq.core.adapter;

import com.jimistore.boot.nemo.mq.core.enums.QueueType;

public class MQMessage {
	 
	/**
	 * 队列名称
	 */
	private String mQName;
	
	/**
	 * 数据源
	 */
	private String dataSource;
	
	/**
	 * 消息内容
	 */
	private Object content;
	
	/**
	 * 发送消息类型
	 */
	private QueueType queueType;
	
	/**
	 * 延时时间
	 */
	private long delayTime;

	public String getmQName() {
		return mQName;
	}

	public MQMessage setmQName(String mQName) {
		this.mQName = mQName;
		return this;
	}

	public String getDataSource() {
		return dataSource;
	}

	public MQMessage setDataSource(String dataSource) {
		this.dataSource = dataSource;
		return this;
	}

	public Object getContent() {
		return content;
	}

	public MQMessage setContent(Object content) {
		this.content = content;
		return this;
	}

	public QueueType getQueueType() {
		return queueType;
	}

	public MQMessage setQueueType(QueueType queueType) {
		this.queueType = queueType;
		return this;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public MQMessage setDelayTime(long delayTime) {
		this.delayTime = delayTime;
		return this;
	}

	
}

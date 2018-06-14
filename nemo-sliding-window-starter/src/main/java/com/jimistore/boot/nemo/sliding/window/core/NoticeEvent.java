package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;

public class NoticeEvent<T extends Number> implements INoticeEvent<T> {
	
	String topicKey;
	
	Long time;
	
	List<T> value;

	public String getTopicKey() {
		return topicKey;
	}

	public NoticeEvent<T> setTopicKey(String topicKey) {
		this.topicKey = topicKey;
		return this;
	}

	public Long getTime() {
		return time;
	}

	public NoticeEvent<T> setTime(Long time) {
		this.time = time;
		return this;
	}

	public List<T> getValue() {
		return value;
	}

	public NoticeEvent<T> setValue(List<T> value) {
		this.value = value;
		return this;
	}
	
	

}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;

public class Channel implements IChannel, Comparable<Channel> {
	
	List<String> topicList;
	
	Long nextTime;
	
	ISubscriber subscriber;

	public Long getNextTime() {
		return nextTime;
	}

	public Channel setNextTime(Long nextTime) {
		this.nextTime = nextTime;
		return this;
	}

	@Override
	public int compareTo(Channel o) {
		if(nextTime>o.getNextTime()){
			return 1;
		}else if(nextTime<o.getNextTime()){
			return -1;
		}
		return 0;
	}

	@Override
	public boolean ready() {
		Long now = System.currentTimeMillis();
		if(now>nextTime){
			Integer interval = subscriber.getInterval();
			if(interval==null||interval==0){
				interval = subscriber.getLength();
			}
			nextTime = nextTime + subscriber.getTimeUnit().toMillis(interval);
			return true;
		}
		return false;
	}

	public ISubscriber getSubscriber() {
		return subscriber;
	}

	public Channel setSubscriber(ISubscriber subscriber) {
		this.subscriber = subscriber;
		return this;
	}

	public List<String> getTopicList() {
		return topicList;
	}

	public Channel setTopicList(List<String> topicList) {
		this.topicList = topicList;
		return this;
	}

	
}

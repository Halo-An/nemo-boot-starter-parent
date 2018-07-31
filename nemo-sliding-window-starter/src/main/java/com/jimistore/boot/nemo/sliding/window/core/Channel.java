package com.jimistore.boot.nemo.sliding.window.core;

public class Channel implements IChannel, Comparable<Channel> {
	
	String topicKey;
	
	Long nextTime;
	
	ISubscriber subscriber;

	public String getTopicKey() {
		return topicKey;
	}

	public Channel setTopicKey(String topicKey) {
		this.topicKey = topicKey;
		return this;
	}

	public Long getNextTime() {
		return nextTime;
	}

	public Channel setNextTime(Long nextTime) {
		this.nextTime = nextTime;
		return this;
	}

	public ISubscriber getSubscriber() {
		return subscriber;
	}

	public Channel setSubscriber(ISubscriber subscriber) {
		this.subscriber = subscriber;
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

	
}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TopicContainer implements ITopicContainer {
	
	protected Map<String, Topic> topicMap = new HashMap<String, Topic>();
	
	@Override
	public Collection<Topic> list() {
		return topicMap.values();
	}

	@Override
	public void create(Topic topic) {
		if(topicMap.containsKey(topic.getKey())){
			throw new RuntimeException(String.format("topic[%s] exist", topic.getKey()));
		}
		topicMap.put(topic.getKey(), topic);
	}

	@Override
	public void delete(String topic) {
		if(!topicMap.containsKey(topic)){
			throw new RuntimeException(String.format("topic[%s] exist", topic));
		}
		topicMap.remove(topic);
	}

}

package com.jimistore.boot.nemo.sliding.window.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cq.nemo.core.exception.ValidatedException;
import com.jimistore.boot.nemo.sliding.window.core.IPublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.Publisher;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;
import com.jimistore.boot.nemo.sliding.window.core.Topic;

public class PublisherHelper {
	
	private static final Logger log = Logger.getLogger(PublisherHelper.class);
		
	SlidingWindowTemplate slidingWindowTemplate;
	
	String service;
	
	public PublisherHelper setService(String service) {
		this.service = service;
		return this;
	}

	public String getService() {
		return service;
	}

	public PublisherHelper setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}
	
	public void createPublisher(Method method){
		slidingWindowTemplate.createPublisher(new Publisher()
				.setKey(PublisherUtil.getPublisherKeyByMethod(method))
				.setService(service));
	}

	
	public void createCounter(String key, TimeUnit timeUnit, int capacity, Class<?> valueType){
		try{
			log.debug(String.format("create counter[%s] when it do not exist", key));
			slidingWindowTemplate.createCounter(key, timeUnit, capacity, valueType);
		}catch(ValidatedException e){
			log.warn(e.getMessage(), e);
		}
		
	}
	
	public void publish(IPublishEvent<?> event){
		slidingWindowTemplate.publish(event); 
	}

	public List<Topic> listTopicByPublisher(String publisherKey) {
		List<Topic> list = new ArrayList<Topic>();
		Collection<Topic> topicList = slidingWindowTemplate.listTopic();
		for(Topic topic:topicList){
			if(topic.getPublisherKey().equals(publisherKey)){
				String key = topic.getKey();
				if(key.indexOf("\"")!=0){
					topic.setKey(String.format("\"%s\"", key));
				}
				list.add(topic);
			}
		}
		return list;
	}

}

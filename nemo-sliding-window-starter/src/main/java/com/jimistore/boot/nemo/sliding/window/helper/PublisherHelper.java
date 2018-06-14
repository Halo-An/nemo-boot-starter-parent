package com.jimistore.boot.nemo.sliding.window.helper;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cq.nemo.core.exception.ValidatedException;
import com.jimistore.boot.nemo.sliding.window.core.IPublishEvent;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;

public class PublisherHelper {
	
	private static final Logger log = Logger.getLogger(PublisherHelper.class);
	
	private Set<String> counterSet =new HashSet<String>();
	
	SlidingWindowTemplate slidingWindowTemplate;
	
	public PublisherHelper setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}

	
	public void createCounter(String key, TimeUnit timeUnit, int capacity, Class<?> valueType){

			if(!counterSet.contains(key)){
				counterSet.add(key);
				try{
					slidingWindowTemplate.createCounter(key, timeUnit, capacity, valueType);
				}catch(ValidatedException e){
					log.warn(e.getMessage(), e);
				}
			}
			
		
	}
	
	public void publish(IPublishEvent<?> event){
		slidingWindowTemplate.publish(event); 
	}

}

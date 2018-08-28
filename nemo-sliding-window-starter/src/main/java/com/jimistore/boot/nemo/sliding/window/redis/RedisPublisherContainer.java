package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.IPublisherContainer;
import com.jimistore.boot.nemo.sliding.window.core.Publisher;
import com.jimistore.boot.nemo.sliding.window.core.PublisherContainer;

public class RedisPublisherContainer extends PublisherContainer implements IPublisherContainer,IRedisSyncTask {
	
	private static final Logger log = Logger.getLogger(RedisPublisherContainer.class);
	
	SlidingWindowProperties slidingWindowProperties;
	
	@SuppressWarnings("rawtypes")
	RedisTemplate redisTemplate;

	ObjectMapper objectMapper;

	
	public RedisPublisherContainer setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public RedisPublisherContainer setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	@SuppressWarnings("rawtypes")
	public RedisPublisherContainer setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void create(Publisher publisher) {
		log.debug("request create");
		if(!publisherMap.containsKey(publisher.getKey())){
			try {
				redisTemplate.opsForHash().put(slidingWindowProperties.getRedisPublisherKey(), publisher.getKey(), objectMapper.writeValueAsString(publisher));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
		super.create(publisher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void delete(String publisher) {
		log.debug("request delete");
		if(publisherMap.containsKey(publisher)){
			redisTemplate.opsForHash().delete(slidingWindowProperties.getRedisPublisherKey(), publisher);
		}
		super.delete(publisher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sync() {
		log.debug("request sync");
		Map<String, String> src = redisTemplate.opsForHash().entries(slidingWindowProperties.getRedisPublisherKey());
		Map<String, Publisher> remoteMap = new HashMap<String, Publisher>();
		for(Entry<String,String> entry:src.entrySet()){
			try {
				remoteMap.put(entry.getKey(), objectMapper.readValue(entry.getValue(), Publisher.class));
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
		synchronized (publisherMap) {
			publisherMap = remoteMap;
		}
	}

}

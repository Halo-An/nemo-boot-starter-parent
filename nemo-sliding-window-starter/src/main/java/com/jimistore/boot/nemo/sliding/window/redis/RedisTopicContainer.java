package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.ITopicContainer;
import com.jimistore.boot.nemo.sliding.window.core.Topic;
import com.jimistore.boot.nemo.sliding.window.core.TopicContainer;

public class RedisTopicContainer extends TopicContainer implements ITopicContainer, IRedisSyncTask {

	private static final Logger LOG = LoggerFactory.getLogger(RedisPublisherContainer.class);

	SlidingWindowProperties slidingWindowProperties;

	@SuppressWarnings("rawtypes")
	RedisTemplate redisTemplate;

	ObjectMapper objectMapper;

	public RedisTopicContainer setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public RedisTopicContainer setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	@SuppressWarnings("rawtypes")
	public RedisTopicContainer setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITopicContainer createTopic(Topic topic) {
		LOG.debug("request create");
		if (!topicMap.containsKey(topic.getKey()) && !topic.isFixed()) {
			try {
				redisTemplate.opsForHash()
						.put(slidingWindowProperties.getRedisTopicKey(), topic.getKey(),
								objectMapper.writeValueAsString(topic));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
		super.createTopic(topic);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ITopicContainer deleteTopic(String topic) {
		LOG.debug("request delete");
		if (topicMap.containsKey(topic)) {
			if (!topicMap.get(topic).isFixed()) {
				redisTemplate.opsForHash().delete(slidingWindowProperties.getRedisTopicKey(), topic);
			}
		}
		super.deleteTopic(topic);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sync() {
		LOG.debug("request sync");

		Map<String, String> src = redisTemplate.opsForHash().entries(slidingWindowProperties.getRedisTopicKey());
		Map<String, Topic> remoteMap = new HashMap<String, Topic>();
		for (Entry<String, String> entry : src.entrySet()) {
			try {
				remoteMap.put(entry.getKey(), objectMapper.readValue(entry.getValue(), Topic.class));
			} catch (Exception e) {
				LOG.warn(e.getMessage(), e);
			}
		}
		topicMap = remoteMap;

	}

}

package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.ICounterContainer;
import com.jimistore.boot.nemo.sliding.window.core.LocalCounterContainer;

public class RedisCounterContainer extends LocalCounterContainer {
	
	private RedisTemplate<?,?> redisTemplate;
	
	private SlidingWindowProperties slidingWindowProperties;

	public RedisCounterContainer setRedisTemplate(RedisTemplate<?, ?> redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	public RedisCounterContainer setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	@Override
	public ICounterContainer createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		RedisCounter<?> counter = RedisCounter.create(slidingWindowProperties, redisTemplate, key, timeUnit, capacity, valueType);

		counterMap.put(key, counter);
		classMap.put(key, valueType);
		Thread thread = new Thread(counter);
		thread.setName(String.format("nemo-sliding-window-redis-counter-%s", key));
		thread.setDaemon(true);
		thread.start();
		return this;
	}

}

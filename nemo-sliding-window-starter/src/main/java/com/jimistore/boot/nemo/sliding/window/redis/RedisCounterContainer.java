package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.ICounter;
import com.jimistore.boot.nemo.sliding.window.core.ICounterContainer;
import com.jimistore.boot.nemo.sliding.window.core.LocalCounterContainer;

/**
 * 处理计数器容器的同步
 * 
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisCounterContainer extends LocalCounterContainer {

	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;

	private SlidingWindowProperties slidingWindowProperties;

	private ObjectMapper objectMapper;

	public static final Map<String, TimeUnit> timeUnitMap = new HashMap<String, TimeUnit>();

	static {
		for (TimeUnit timeUnit : TimeUnit.values()) {
			timeUnitMap.put(timeUnit.toString(), timeUnit);
		}
	}

	public RedisCounterContainer() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public RedisCounterContainer setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	public RedisCounterContainer setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	public RedisCounterContainer setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ICounterContainer createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		this.createCounterNotRedis(key, timeUnit, capacity, valueType);

		// 同步给redis
		CounterMsg counter = new CounterMsg().setCapacity(capacity).setKey(key).setClassName(valueType.getName())
				.setTimeUnit(timeUnit.toString());

		try {
			redisTemplate.opsForHash().put(slidingWindowProperties.getRedisContainerKey(), key,
					objectMapper.writeValueAsString(counter));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		redisTemplate.expire(slidingWindowProperties.getRedisContainerKey(), slidingWindowProperties.getRedisExpired(),
				TimeUnit.MILLISECONDS);
		return this;
	}

	private ICounterContainer createCounterNotRedis(String key, TimeUnit timeUnit, Integer capacity,
			Class<?> valueType) {
		RedisCounter<?> counter = RedisCounter.create(slidingWindowProperties, redisTemplate, key, timeUnit, capacity,
				valueType);
		counterMap.put(key, counter);
		return this;
	}

	private ICounterContainer createCounter(CounterMsg counter) {
		try {
			return this.createCounterNotRedis(counter.getKey(), timeUnitMap.get(counter.getTimeUnit()), counter.getCapacity(),
					Class.forName(counter.getClassName()));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected void sync() {
		// 排队同步
		try {
			queue.put(new Runnable() {

				@SuppressWarnings({ "unchecked" })
				@Override
				public void run() {
					// 同步计数容器
					Map<String, String> src = redisTemplate.opsForHash()
							.entries(slidingWindowProperties.getRedisContainerKey());
					for (String key : src.keySet()) {
						if (!counterMap.containsKey(key)) {
							try {
								String content = src.get(key);
								CounterMsg counter = objectMapper.readValue(content, CounterMsg.class);
								createCounter(counter);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					// 同步计数
					for (Entry<String, ICounter<?>> entry : counterMap.entrySet()) {
						RedisCounter<?> counter = (RedisCounter<?>) entry.getValue();
						counter.sync();
					}
				}

			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}

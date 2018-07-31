package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.ArrayList;
import java.util.List;
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

		RedisCounter<?> counter = RedisCounter.create(slidingWindowProperties, redisTemplate, key, timeUnit, capacity,
				valueType);
		counterMap.put(key, counter);

		// 同步给redis
		CounterMsg counterMsg = new CounterMsg().setCapacity(capacity).setKey(key).setClassName(valueType.getName())
				.setTimeUnit(timeUnit.toString());

		try {
			boolean result = redisTemplate.opsForHash().putIfAbsent(slidingWindowProperties.getRedisContainerKey(), key,
					objectMapper.writeValueAsString(counterMsg));
			if(result){
				redisTemplate.expire(slidingWindowProperties.getRedisContainerKey(), slidingWindowProperties.getRedisExpired(),
						TimeUnit.MILLISECONDS);
			}else{
				counter.sync();
			}
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	@SuppressWarnings({ "unchecked" })
	protected List<CounterMsg> getNotExistCounterList(){
		List<CounterMsg> counterMsgList = new ArrayList<CounterMsg>();
		// 同步计数容器
		Map<String, String> src = redisTemplate.opsForHash()
				.entries(slidingWindowProperties.getRedisContainerKey());
		for (String key : src.keySet()) {
			if (!counterMap.containsKey(key)) {
				try {
					String content = src.get(key);
					CounterMsg counter = objectMapper.readValue(content, CounterMsg.class);
					counterMsgList.add(counter);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return counterMsgList;
		
	}

	protected void sync() {
		// 同步计数
		for (Entry<String, ICounter<?>> entry : counterMap.entrySet()) {
			RedisCounter<?> counter = (RedisCounter<?>) entry.getValue();
			counter.sync();
		}
	}

}

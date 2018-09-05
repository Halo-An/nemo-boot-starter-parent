package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.cq.nemo.core.exception.ValidatedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.ICounter;
import com.jimistore.boot.nemo.sliding.window.core.ICounterContainer;
import com.jimistore.boot.nemo.sliding.window.core.LocalCounterContainer;
import com.jimistore.boot.nemo.sliding.window.core.Topic;

/**
 * 处理计数器容器的同步
 * 
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisCounterContainer extends LocalCounterContainer implements IRedisSyncTask {

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
	public ICounterContainer createCounter(Topic topic) {
		if(counterMap.containsKey(topic.getKey())){
			throw new ValidatedException(String.format("counter[%s] is exist", topic.getKey()));
		}
		RedisCounter<?> counter = RedisCounter.create(slidingWindowProperties, 
				redisTemplate, 
				topic.getKey(), 
				topic.getTimeUnit(), 
				topic.getCapacity(),
				topic.getValueType());
		counterMap.put(topic.getKey(), counter);

		// 同步给redis
		CounterMsg counterMsg = new CounterMsg().setCapacity(topic.getCapacity()).setKey(topic.getKey()).setClassName(topic.getClassName())
				.setTimeUnit(topic.getTimeUnitStr());

		try {
			boolean result = redisTemplate.opsForHash().putIfAbsent(slidingWindowProperties.getRedisContainerKey(), topic.getKey(),
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

	@SuppressWarnings("unchecked")
	@Override
	public ICounterContainer deleteCounter(String key) {
		if(counterMap.containsKey(key)){
			redisTemplate.opsForHash().delete(slidingWindowProperties.getRedisContainerKey(), key);
		}
		super.deleteCounter(key);
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
	
	@SuppressWarnings({ "unchecked" })
	protected List<ICounter<?>> getOverflowCounterList(){
		List<ICounter<?>> counterMsgList = new ArrayList<ICounter<?>>();
		// 同步计数容器
		Map<String, String> src = redisTemplate.opsForHash()
				.entries(slidingWindowProperties.getRedisContainerKey());
		
		for (String key : counterMap.keySet()) {
			if (!src.containsKey(key)) {
				counterMsgList.add(counterMap.get(key));
			}
		}
		return counterMsgList;
		
	}

	public void sync() {
		// 同步计数
		for (Entry<String, ICounter<?>> entry : counterMap.entrySet()) {
			RedisCounter<?> counter = (RedisCounter<?>) entry.getValue();
			counter.sync();
		}
	}

}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.exception.ConfigException;
import com.jimistore.boot.nemo.sliding.window.handler.INoticeHandler;
import com.jimistore.boot.nemo.sliding.window.handler.IPublishHandler;
import com.jimistore.boot.nemo.sliding.window.redis.RedisCounterContainer;
import com.jimistore.boot.nemo.sliding.window.redis.RedisDispatcher;

/**
 * 调度中心代理类-主入口
 * @author chenqi
 * @Date 2018年6月6日
 *
 */
public class SlidingWindowTemplate implements IDispatcher {
	
	SlidingWindowProperties slidingWindowProperties;
	
	IDispatcher dispatcher;
	
	private SlidingWindowTemplate(){
		
	}
	
	private SlidingWindowTemplate setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}


	private SlidingWindowTemplate setDispatcher(IDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		return this;
	}
	
	public static final SlidingWindowTemplate create(SlidingWindowProperties slidingWindowProperties){
		return create(slidingWindowProperties, null, null);
	}

	public static final SlidingWindowTemplate create(SlidingWindowProperties slidingWindowProperties, RedisTemplate<?,?> redisTemplate, ObjectMapper objectMapper){
		if(slidingWindowProperties==null){
			throw new ConfigException("sliding window properties connot be null");
		}
		Dispatcher dispatcher = null;
		if(!StringUtils.isEmpty(slidingWindowProperties.getCacheModel())&&slidingWindowProperties.getCacheModel().equals(SlidingWindowProperties.CACHE_MODEL_REDIS)){
			dispatcher = new RedisDispatcher()
					.setSlidingWindowProperties(slidingWindowProperties)
					.setRedisCounterContainer(new RedisCounterContainer()
							.setObjectMapper(objectMapper)
							.setRedisTemplate(redisTemplate)
							.setSlidingWindowProperties(slidingWindowProperties))
					.setChannelContainer(new ChannelContainer());
		}else{
			dispatcher = new Dispatcher()
					.setCounterContainer(new LocalCounterContainer())
					.setChannelContainer(new ChannelContainer());
		}
		return new SlidingWindowTemplate().setSlidingWindowProperties(slidingWindowProperties).setDispatcher(dispatcher);
		
	}

	@Override
	public SlidingWindowTemplate subscribe(ISubscriber subscriber) {
		dispatcher.subscribe(subscriber);
		return this;
		
	}

	@Override
	public SlidingWindowTemplate publish(IPublishEvent<?> event) {
		dispatcher.publish(event);
		return this;
	}

	@Override
	public SlidingWindowTemplate addPublishHandler(IPublishHandler publishHandler) {
		dispatcher.addPublishHandler(publishHandler);
		return this;
	}

	@Override
	public SlidingWindowTemplate addNoticeHandler(INoticeHandler noticeHandler) {
		dispatcher.addNoticeHandler(noticeHandler);
		return this;
	}

	@Override
	public SlidingWindowTemplate createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		dispatcher.createCounter(key, timeUnit, capacity, valueType);
		return this;
	}
}

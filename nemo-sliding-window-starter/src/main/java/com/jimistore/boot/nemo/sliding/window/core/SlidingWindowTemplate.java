package com.jimistore.boot.nemo.sliding.window.core;

import java.util.Collection;
import java.util.List;
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
import com.jimistore.boot.nemo.sliding.window.redis.RedisPublisherContainer;
import com.jimistore.boot.nemo.sliding.window.redis.RedisTopicContainer;

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
					.setCounterContainer(new RedisCounterContainer()
							.setObjectMapper(objectMapper)
							.setRedisTemplate(redisTemplate)
							.setSlidingWindowProperties(slidingWindowProperties))
					.setChannelContainer(new ChannelContainer())
					.setPublisherContainer(new RedisPublisherContainer()
							.setObjectMapper(objectMapper)
							.setRedisTemplate(redisTemplate)
							.setSlidingWindowProperties(slidingWindowProperties))
					.setTopicContainer(new RedisTopicContainer()
							.setObjectMapper(objectMapper)
							.setRedisTemplate(redisTemplate)
							.setSlidingWindowProperties(slidingWindowProperties))
					.setSlidingWindowProperties(slidingWindowProperties)
					.init();
		}else{
			dispatcher = new Dispatcher()
					.setCounterContainer(new LocalCounterContainer())
					.setChannelContainer(new ChannelContainer())
					.setPublisherContainer(new PublisherContainer())
					.setTopicContainer(new TopicContainer())
					.setSlidingWindowProperties(slidingWindowProperties)
					.init();
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

	@Override
	public IDispatcher createPublisher(Publisher publisher) {
		return dispatcher.createPublisher(publisher);
	}

	@Override
	public Collection<Topic> listTopic() {
		return dispatcher.listTopic();
	}

	@Override
	public Collection<Publisher> listPublisher() {
		return dispatcher.listPublisher();
	}

	@Override
	public IDispatcher deletePublisher(String publisher) {
		dispatcher.deletePublisher(publisher);
		return this;
	}

	@Override
	public IDispatcher createTopic(Topic topic) {
		dispatcher.createTopic(topic);
		return this;
	}

	@Override
	public IDispatcher deleteTopic(String topic) {
		dispatcher.deleteTopic(topic);
		return this;
	}

	@Override
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		return dispatcher.window(key, timeUnit, length, valueType);
	}

	@Override
	public <E> List<List<E>> listWindow(String key, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		return dispatcher.listWindow(key, timeUnit, length, valueType);
	}
}

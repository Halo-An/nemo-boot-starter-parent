package com.jimistore.boot.nemo.sliding.window.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cq.nemo.core.exception.ValidatedException;

/**
 * 计数容器
 * @author chenqi
 * @Date 2018年6月1日
 *
 */
public class LocalCounterContainer extends Thread implements ICounterContainer {
	
	private static final Logger log = Logger.getLogger(LocalCounterContainer.class);
	
	protected Map<String, ICounter<?>> counterMap = new HashMap<String, ICounter<?>>();
	
	protected Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
	
	LinkedBlockingQueue<IPublishEvent<?>> queue = new LinkedBlockingQueue<IPublishEvent<?>>();
	
	public LocalCounterContainer(){
		this.init();
	}
	
	private void init(){
		super.setName("sliding-window-counter-container-queue");
		super.setDaemon(true);
		this.start();
	}

	@Override
	public ICounterContainer put(IPublishEvent<?> event) {
		if(log.isDebugEnabled()){
			log.debug("request put");
		}
		if(event==null){
			throw new ValidatedException("event can not be null");
		}
		try {
			queue.put(event);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	@Override
	public ICounterContainer createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		if(counterMap.containsKey(key)){
			throw new ValidatedException(String.format("counter[%s] is exist", key));
		}
		ICounter<?> counter = Counter.create(key, timeUnit, capacity, valueType);
		counterMap.put(key, counter);
		classMap.put(key, valueType);
		return this;
	}
	
	protected ICounter<?> getCounterByKey(String key){
		if(key==null){
			throw new ValidatedException("key of event can not be null");
		}
		
		ICounter<?> counter = counterMap.get(key);
		if(counter==null){
			throw new ValidatedException(String.format("event can not find counter[%s]", key));
		}
		return counter;
	}

	@Override
	public <E> List<E> window(String key, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		ICounter<?> counter = this.getCounterByKey(key);
		return counter.window(timeUnit, length, valueType);
	}

	@Override
	public void run() {
		while(true){
			try{
				IPublishEvent<?> event = queue.take();
				ICounter<?> counter = this.getCounterByKey(event.getTopicKey());
				counter.put(event);
			}catch(Exception e){
				log.debug(e);
			}
		}
	}

	@Override
	public Set<String> getAllKeys() {
		
		return counterMap.keySet();
	}
	
}

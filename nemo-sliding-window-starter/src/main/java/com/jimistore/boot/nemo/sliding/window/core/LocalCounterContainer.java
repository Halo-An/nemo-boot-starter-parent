package com.jimistore.boot.nemo.sliding.window.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cq.nemo.core.exception.ValidatedException;

/**
 * 计数容器
 * @author chenqi
 * @Date 2018年6月1日
 *
 */
public class LocalCounterContainer implements ICounterContainer {
	
	private static final Logger log = Logger.getLogger(LocalCounterContainer.class);
	
	protected Map<String, ICounter<?>> counterMap = new HashMap<String, ICounter<?>>();
		
	public LocalCounterContainer(){
	}

	@Override
	public ICounterContainer put(IPublishEvent<?> event) {
		if(log.isDebugEnabled()){
			log.debug(String.format("request put[%s]", event.getTopicKey()));
		}
		if(event==null){
			throw new ValidatedException("event can not be null");
		}
		ICounter<?> counter = getCounterByKey(event.getTopicKey());
		counter.put(event);
		return this;
	}

	@Override
	public ICounterContainer createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {

		if(counterMap.containsKey(key)){
			throw new ValidatedException(String.format("counter[%s] is exist", key));
		}
		ICounter<?> counter = Counter.create(key, timeUnit, capacity, valueType);
		counterMap.put(key, counter);
		
		return this;
	}

	@Override
	public ICounterContainer deleteCounter(String key) {
		counterMap.remove(key);
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
	
	public void heartbeat(){
		for(Entry<String, ICounter<?>> entry:counterMap.entrySet()){
			entry.getValue().heartbeat();
		}
	}

	@Override
	public Set<String> getAllKeys() {
		synchronized (counterMap) {
			return counterMap.keySet();
		}
	}

	@Override
	public <E> List<List<E>> listWindow(String key, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		ICounter<?> counter = this.getCounterByKey(key);
		return counter.listWindow(timeUnit, length, valueType);
	}
	
}

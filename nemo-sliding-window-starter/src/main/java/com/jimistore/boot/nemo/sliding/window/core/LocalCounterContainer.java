package com.jimistore.boot.nemo.sliding.window.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
public class LocalCounterContainer implements ICounterContainer {
	
	private static final Logger log = Logger.getLogger(LocalCounterContainer.class);
	
	protected Map<String, ICounter<?>> counterMap = new HashMap<String, ICounter<?>>();
		
	protected LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	
	//队列线程
	Thread queueThread = new Thread("nemo-sliding-window-counter-container-queue"){

		@Override
		public void run() {
			while(true){
				try {
					Runnable task = queue.take();
					task.run();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	};
		
	public LocalCounterContainer(){
		queueThread.setDaemon(true);
		queueThread.start();
	}

	@Override
	public ICounterContainer put(IPublishEvent<?> event) {
		if(log.isDebugEnabled()){
			log.debug(String.format("request put[%s]", event.getTopicKey()));
		}
		if(event==null){
			throw new ValidatedException("event can not be null");
		}
		
		//排队发布数据
		try {
			queue.put(new Runnable(){

				@Override
				public void run() {
					ICounter<?> counter = getCounterByKey(event.getTopicKey());
					counter.put(event);
				}
				
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	@Override
	public ICounterContainer createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		//排队心跳
		try {
			queue.put(new Runnable(){

				@Override
				public void run() {
					if(counterMap.containsKey(key)){
						throw new ValidatedException(String.format("counter[%s] is exist", key));
					}
					ICounter<?> counter = Counter.create(key, timeUnit, capacity, valueType);
					counterMap.put(key, counter);
				}
				
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		
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
		//排队心跳
		try {
			queue.put(new Runnable(){

				@Override
				public void run() {
					for(Entry<String, ICounter<?>> entry:counterMap.entrySet()){
						entry.getValue().heartbeat();
					}
				}
				
			});
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<String> getAllKeys() {
		
		return counterMap.keySet();
	}
	
}

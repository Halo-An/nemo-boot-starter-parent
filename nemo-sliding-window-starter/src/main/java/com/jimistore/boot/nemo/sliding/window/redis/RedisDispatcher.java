package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.sliding.window.core.Dispatcher;

/**
 * 同步redis数据
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisDispatcher extends Dispatcher {
	
	private static final Logger log = Logger.getLogger(RedisDispatcher.class);
	
	RedisCounterContainer redisCounterContainer;

	public static final Map<String, TimeUnit> timeUnitMap = new HashMap<String, TimeUnit>();

	static {
		for (TimeUnit timeUnit : TimeUnit.values()) {
			timeUnitMap.put(timeUnit.toString(), timeUnit);
		}
	}
	
	//同步远端数据线程
	Thread syncThread = new Thread("nemo-sliding-window-redis-sync"){
		@Override
		public void run() {
			while(true){
				try {
					sync();
					Thread.sleep(slidingWindowProperties.getSyncInterval());
				} catch (Exception e) {
					log.error("redis sync error", e);
				}
			}
		}
	};

	public RedisDispatcher() {
		super();
	}
	
	public RedisDispatcher setRedisCounterContainer(RedisCounterContainer redisCounterContainer) {
		this.redisCounterContainer = redisCounterContainer;
		super.setCounterContainer(redisCounterContainer);
		return this;
	}

	protected void sync(){
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if(redisCounterContainer!=null){
				log.debug("request sync");
				//同步计数容器
				List<CounterMsg> counterList = redisCounterContainer.getNotExistCounterList();
				for(CounterMsg counterMsg:counterList){
					try {
						createCounter(counterMsg.getKey(), timeUnitMap.get(counterMsg.getTimeUnit()), counterMsg.getCapacity(), Class.forName(counterMsg.getClassName()));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
				//同步计数
				redisCounterContainer.sync();
			}
			}
		});
		
	}

	@Override
	public RedisDispatcher init() {
		super.init();
		syncThread.setDaemon(true);
		syncThread.start();
		return this;
	}

}

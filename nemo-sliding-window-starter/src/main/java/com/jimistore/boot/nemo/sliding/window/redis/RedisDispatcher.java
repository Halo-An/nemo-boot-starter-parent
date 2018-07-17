package com.jimistore.boot.nemo.sliding.window.redis;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
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
	
	SlidingWindowProperties slidingWindowProperties;
	
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
		syncThread.setDaemon(true);
		syncThread.start();
	}
	
	public RedisDispatcher setRedisCounterContainer(RedisCounterContainer redisCounterContainer) {
		this.redisCounterContainer = redisCounterContainer;
		super.setCounterContainer(redisCounterContainer);
		return this;
	}

	public RedisDispatcher setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	protected void sync(){
		if(redisCounterContainer!=null){
			log.debug("request sync");
			redisCounterContainer.sync();
		}
	}

}

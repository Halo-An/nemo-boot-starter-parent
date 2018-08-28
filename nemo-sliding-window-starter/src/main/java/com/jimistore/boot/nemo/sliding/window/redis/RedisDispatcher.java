package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.sliding.window.core.Dispatcher;
import com.jimistore.boot.nemo.sliding.window.core.ICounter;
import com.jimistore.boot.nemo.sliding.window.core.ICounterContainer;
import com.jimistore.boot.nemo.sliding.window.core.IPublisherContainer;
import com.jimistore.boot.nemo.sliding.window.core.ITopicContainer;

/**
 * 同步redis数据
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisDispatcher extends Dispatcher implements IRedisSyncTask {
	
	private static final Logger log = Logger.getLogger(RedisDispatcher.class);

	public static final Map<String, TimeUnit> timeUnitMap = new HashMap<String, TimeUnit>();
	
	List<IRedisSyncTask> redisSyncTaskList = new ArrayList<IRedisSyncTask>();

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
					if(redisSyncTaskList!=null){
						for(IRedisSyncTask redisSyncTask:redisSyncTaskList){
							createQueueTask(new Runnable() {
								@Override
								public void run() {
									redisSyncTask.sync();
								}
							});
						}
					}
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

	public RedisDispatcher setRedisSyncTaskList(List<IRedisSyncTask> redisSyncTaskList) {
		this.redisSyncTaskList = redisSyncTaskList;
		return this;
	}

	public void sync(){
		if(counterContainer!=null&&counterContainer instanceof RedisCounterContainer){
			RedisCounterContainer redisCounterContainer = (RedisCounterContainer) counterContainer;
			log.debug("request sync");
			//同步缺少的计数器
			List<CounterMsg> counterMsgList = redisCounterContainer.getNotExistCounterList();
			for(CounterMsg counterMsg:counterMsgList){
				try {
					createCounter(counterMsg.getKey(), timeUnitMap.get(counterMsg.getTimeUnit()), counterMsg.getCapacity(), Class.forName(counterMsg.getClassName()));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			//同步已经被删除的计数器
			List<ICounter<?>> counterList = redisCounterContainer.getOverflowCounterList();
			for(ICounter<?> counter:counterList){
				redisCounterContainer.deleteCounter(counter.getKey());
			}
		}
		
	}

	@Override
	public RedisDispatcher init() {
		super.init();
		
		this.addSyncTask(this);
		
		syncThread.setDaemon(true);
		syncThread.start();
		return this;
	}
	
	public void addSyncTask(IRedisSyncTask task){
		redisSyncTaskList.add(task);
	}

	@Override
	public Dispatcher setCounterContainer(ICounterContainer counterContainer) {
		super.setCounterContainer(counterContainer);
		if(counterContainer instanceof IRedisSyncTask){
			this.addSyncTask((IRedisSyncTask)counterContainer);
		}
		return this;
	}

	@Override
	public Dispatcher setPublisherContainer(IPublisherContainer publisherContainer) {
		super.setPublisherContainer(publisherContainer);
		if(publisherContainer instanceof IRedisSyncTask){
			this.addSyncTask((IRedisSyncTask)publisherContainer);
		}
		return this;
	}

	@Override
	public Dispatcher setTopicContainer(ITopicContainer topicContainer) {
		super.setTopicContainer(topicContainer);
		if(topicContainer instanceof IRedisSyncTask){
			this.addSyncTask((IRedisSyncTask)topicContainer);
		}
		return this;
	}

}

package com.jimistore.boot.nemo.sliding.window.redis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimistore.boot.nemo.sliding.window.core.Dispatcher;
import com.jimistore.boot.nemo.sliding.window.core.ICounter;
import com.jimistore.boot.nemo.sliding.window.core.ICounterContainer;
import com.jimistore.boot.nemo.sliding.window.core.IPublisherContainer;
import com.jimistore.boot.nemo.sliding.window.core.ITopicContainer;
import com.jimistore.boot.nemo.sliding.window.core.Topic;

/**
 * 同步redis数据
 * 
 * @author chenqi
 * @Date 2018年7月17日
 *
 */
public class RedisDispatcher extends Dispatcher implements IRedisSyncTask {

	private static final Logger LOG = LoggerFactory.getLogger(RedisDispatcher.class);

	List<IRedisSyncTask> redisSyncTaskList = new ArrayList<IRedisSyncTask>();

	// 同步远端数据线程
	Thread syncThread = new Thread("nemo-sliding-window-redis-sync") {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(slidingWindowProperties.getSyncInterval());
					if (redisSyncTaskList != null) {
						for (IRedisSyncTask redisSyncTask : redisSyncTaskList) {
							createQueueTask(new Runnable() {
								@Override
								public void run() {
									redisSyncTask.sync();
								}
							});
						}
					}
				} catch (Exception e) {
					LOG.error("redis sync error", e);
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

	public void sync() {
		if (counterContainer != null && counterContainer instanceof RedisCounterContainer) {
			RedisCounterContainer redisCounterContainer = (RedisCounterContainer) counterContainer;
			LOG.debug("request sync");
			// 同步缺少的计数器
			List<CounterMsg> counterMsgList = redisCounterContainer.getNotExistCounterList();
			for (CounterMsg counterMsg : counterMsgList) {
				Topic topic = new Topic().setKey(counterMsg.getKey())
						.setTimeUnitStr(counterMsg.getTimeUnit())
						.setCapacity(counterMsg.getCapacity())
						.setClassName(counterMsg.getClassName());

				createCounter(topic);

			}
			// 同步已经被删除的计数器
			List<ICounter<?>> counterList = redisCounterContainer.getOverflowCounterList();
			for (ICounter<?> counter : counterList) {
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

	public void addSyncTask(IRedisSyncTask task) {
		redisSyncTaskList.add(task);
	}

	@Override
	public Dispatcher setCounterContainer(ICounterContainer counterContainer) {
		super.setCounterContainer(counterContainer);
		if (counterContainer instanceof IRedisSyncTask) {
			this.addSyncTask((IRedisSyncTask) counterContainer);
		}
		return this;
	}

	@Override
	public Dispatcher setPublisherContainer(IPublisherContainer publisherContainer) {
		super.setPublisherContainer(publisherContainer);
		if (publisherContainer instanceof IRedisSyncTask) {
			this.addSyncTask((IRedisSyncTask) publisherContainer);
		}
		return this;
	}

	@Override
	public Dispatcher setTopicContainer(ITopicContainer topicContainer) {
		super.setTopicContainer(topicContainer);
		if (topicContainer instanceof IRedisSyncTask) {
			this.addSyncTask((IRedisSyncTask) topicContainer);
		}
		return this;
	}

}

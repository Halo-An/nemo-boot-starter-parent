package com.jimistore.boot.nemo.sliding.window.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.handler.INoticeHandler;
import com.jimistore.boot.nemo.sliding.window.handler.IPublishHandler;

/**
 * 调度器
 * @author chenqi
 * @Date 2018年7月19日
 *
 */
public class Dispatcher implements IDispatcher {
	
	private static final Logger log = Logger.getLogger(Dispatcher.class);
	
	public static final Long INTERVAL = 1000l;
	
	protected List<IPublishHandler> publishHandlerList = new ArrayList<IPublishHandler>();
	
	protected List<INoticeHandler> noticeHandlerList = new ArrayList<INoticeHandler>();
	
	protected ICounterContainer counterContainer;
	
	protected IChannelContainer channelContainer;
	
	protected IPublisherContainer publisherContainer;
	
	protected ITopicContainer topicContainer;
	
	protected SlidingWindowProperties slidingWindowProperties;
	
	protected Executor executor;
	
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
					e.printStackTrace();
				}
			}
		}
		
	};
	
	//心跳线程
	Thread heartbeatThread = new Thread("nemo-sliding-window-heartbeat"){
		@Override
		public void run() {
			while(true){
				try {
					heartbeat();
					Thread.sleep(INTERVAL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	};
	
	//窗口调度线程
	Thread schedulerThread = new Thread("nemo-sliding-window-scheduler"){
		@Override
		public void run() {
			while(true){
				try{
					scheduler();
				}catch(Exception e){
					e.printStackTrace();
				}finally{

					try {
						Thread.sleep(INTERVAL);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	};
	
	public Dispatcher(){
	}

	public Dispatcher setSlidingWindowProperties(SlidingWindowProperties slidingWindowProperties) {
		this.slidingWindowProperties = slidingWindowProperties;
		return this;
	}

	public Dispatcher setCounterContainer(ICounterContainer counterContainer) {
		this.counterContainer = counterContainer;
		return this;
	}

	public Dispatcher setChannelContainer(IChannelContainer channelContainer) {
		this.channelContainer = channelContainer;
		return this;
	}

	public Dispatcher setPublisherContainer(IPublisherContainer publisherContainer) {
		this.publisherContainer = publisherContainer;
		return this;
	}

	public Dispatcher setTopicContainer(ITopicContainer topicContainer) {
		this.topicContainer = topicContainer;
		return this;
	}

	public Dispatcher init() {
		heartbeatThread.setDaemon(true);
		schedulerThread.setDaemon(true);
		queueThread.setDaemon(true);
		heartbeatThread.start();
		schedulerThread.start();
		queueThread.start();
		
		executor = Executors.newFixedThreadPool(slidingWindowProperties.getMaxNoticeThreadSize());
		return this;
	}

	@Override
	public Dispatcher subscribe(ISubscriber subscriber) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				channelContainer.put(subscriber);
			}
		});
		return this;
	}

	@Override
	public Dispatcher publish(IPublishEvent<?> event) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				counterContainer.publish(event);
			}
		});
		return this;
	}

	@Override
	public IDispatcher addPublishHandler(IPublishHandler publishHandler) {
		publishHandlerList.add(publishHandler);
		return this;
	}

	@Override
	public IDispatcher addNoticeHandler(INoticeHandler noticeHandler) {
		noticeHandlerList.add(noticeHandler);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	protected void scheduler(){
		if(counterContainer==null){
			return ;
		}
		long eventTime = System.currentTimeMillis();
		try{
			for(String key:counterContainer.getAllCounterKeys()){
				List<IChannel> channelSet = channelContainer.match(key);
				if(channelSet==null){
					break;
				}
				Long now = System.currentTimeMillis();
				for(IChannel channel:channelSet){
					if(channel.getNextTime()>now){
						continue;
					}
					ISubscriber subscriber = channel.getSubscriber();
					Integer interval = subscriber.getInterval();
					if(interval==null||interval==0){
						interval = subscriber.getLength();
					}
					channel.setNextTime(channel.getNextTime()+subscriber.getTimeUnit().toMillis(interval));
					List<Number> value = (List<Number>)counterContainer.window(key, subscriber.getTimeUnit(), subscriber.getLength(), subscriber.getValueType());
					NoticeEvent<Number> event = new NoticeEvent<Number>().setTopicKey(key).setValue(value).setTime(eventTime);
					executor.execute(new Runnable(){
						@Override
						public void run() {
							long old = System.currentTimeMillis();
							try{
								if(log.isDebugEnabled()){
									log.debug(String.format("call notice of subscriber[%s] start", key));
								}
								subscriber.notice(event);
							}finally{
								long diff = System.currentTimeMillis() - old;
								if(log.isDebugEnabled()){
									log.debug(String.format("call notice of subscriber[%s] end, cost time is %s", key, diff));
								}
							}
						}
						
					});
				}
			}
		}finally{
			if(log.isTraceEnabled()){
				log.trace(String.format("request scheduler end , cost time is %s", System.currentTimeMillis() - eventTime));
			}
		}
	}
	
	public void heartbeat(){
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if(counterContainer!=null){
					counterContainer.heartbeat();
				}
			}
		});
	}

	@Override
	public IDispatcher createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				log.debug(String.format("create counter %s", key));
				counterContainer.createCounter(key, timeUnit, capacity, valueType);
				channelContainer.put(key);
			}
		});
		return this;
	}
	
	protected void createQueueTask(Runnable runnable){
		try {
			queue.put(runnable);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IDispatcher deleteTopic(String topic) {
		topicContainer.deleteTopic(topic);
		counterContainer.deleteCounter(topic);
		channelContainer.delete(topic);
		return this;
	}

	@Override
	public IDispatcher deleteCounter(String key) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if(counterContainer!=null){
					counterContainer.deleteCounter(key);
				}
			}
		});
		return this;
	}

}

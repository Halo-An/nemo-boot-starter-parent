package com.jimistore.boot.nemo.sliding.window.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.sliding.window.handler.INoticeHandler;
import com.jimistore.boot.nemo.sliding.window.handler.IPublishHandler;

public class Dispatcher implements IDispatcher {
	
	private static final Logger log = Logger.getLogger(Dispatcher.class);
	
	public static final Long INTERVAL = 1000l;
	
	List<IPublishHandler> publishHandlerList = new ArrayList<IPublishHandler>();
	
	List<INoticeHandler> noticeHandlerList = new ArrayList<INoticeHandler>();
	
	ICounterContainer counterContainer;
	
	IChannelContainer channelContainer;
	
	ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 5, 
			3000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), 
			Executors.defaultThreadFactory(), new CallerRunsPolicy());;
	
	//心跳线程
	Thread heartbeatThread = new Thread("nemo-sliding-window-heartbeat"){
		@Override
		public void run() {
			while(true){
				try {
					heartbeat();
					Thread.sleep(1000l);
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
	};
	
	public Dispatcher(){
		heartbeatThread.setDaemon(true);
		schedulerThread.setDaemon(true);
		heartbeatThread.start();
		schedulerThread.start();
	}

	public Dispatcher setCounterContainer(ICounterContainer counterContainer) {
		this.counterContainer = counterContainer;
		return this;
	}

	public Dispatcher setChannelContainer(IChannelContainer channelContainer) {
		this.channelContainer = channelContainer;
		return this;
	}

	@Override
	public Dispatcher subscribe(ISubscriber subscriber) {
		channelContainer.put(subscriber);
		return this;
	}

	@Override
	public Dispatcher publish(IPublishEvent<?> event) {
		counterContainer.put(event);
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
		for(String key:counterContainer.getAllKeys()){
			Set<IChannel> channelSet = channelContainer.match(key);
			if(channelSet==null){
				break;
			}
			Long now = System.currentTimeMillis();
			for(IChannel channel:channelSet){
				if(channel.getNextTime()>now){
					break;
				}
				ISubscriber subscriber = channel.getSubscriber();
				Integer interval = subscriber.getInterval();
				if(interval==null||interval==0){
					interval = subscriber.getLength();
				}
				channel.setNextTime(channel.getNextTime()+subscriber.getTimeUnit().toMillis(interval));
				List<Number> value = (List<Number>)counterContainer.window(key, subscriber.getTimeUnit(), subscriber.getLength(), subscriber.getValueType());
				threadPoolExecutor.execute(new Runnable(){
					@Override
					public void run() {
						if(log.isDebugEnabled()){
							log.debug(String.format("call notice of subscriber[%s]", key));
						}
						subscriber.notice(new NoticeEvent<Number>().setTopicKey(key).setValue(value).setTime(System.currentTimeMillis()));
					}
					
				});
			}
		}
	}
	
	public void heartbeat(){
		if(counterContainer!=null){
			counterContainer.heartbeat();
		}
	}

	@Override
	public IDispatcher createCounter(String key, TimeUnit timeUnit, Integer capacity, Class<?> valueType) {
		log.debug(String.format("create counter %s", key));
		counterContainer.createCounter(key, timeUnit, capacity, valueType);
		channelContainer.put(key);
		return this;
	}

}

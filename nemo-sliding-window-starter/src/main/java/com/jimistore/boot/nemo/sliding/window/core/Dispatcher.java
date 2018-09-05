package com.jimistore.boot.nemo.sliding.window.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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
	
	/**
	 * 核心驱动线程实现
	 */
	protected void scheduler(){
		if(counterContainer==null){
			return ;
		}
		long eventTime = System.currentTimeMillis();
		try{
			for(IChannel channel:channelContainer.listAllChannel()){
				if(!channel.ready()){
					continue;
				}
				this.execute(eventTime, channel);
			}
		}finally{
			if(log.isTraceEnabled()){
				log.trace(String.format("request scheduler end , cost time is %s", System.currentTimeMillis() - eventTime));
			}
		}
	}
	
	/**
	 * 解析事件数据
	 * @param channel
	 * @param time
	 * @return
	 */	
	protected INoticeEvent<?> parseValue(IChannel channel, Long time){
		ISubscriber subscriber = channel.getSubscriber();
		String key = null;
		List<Number> value = null;
		
		//是否是逻辑主体订阅
		if(subscriber instanceof ILogicSubscriber){
			ILogicSubscriber logicSubscriber = (ILogicSubscriber) subscriber;
			key = logicSubscriber.getTopicMatch();
			value = this.getLogicValue(logicSubscriber);
		}else{
			key = channel.getTopicList().get(0);
			value = this.getValueByBuffered(key, subscriber.getTimeUnit(), subscriber.getLength(), subscriber.getValueType());
		}
		NoticeEvent<Number> event = new NoticeEvent<Number>().setTopicKey(key).setValue(value).setTime(time);
		//是否是预警订阅
		if(subscriber instanceof IWarnSubscriber){
			return new NoticeStatisticsEvent<Number>(event);
		}
		return event;
	}

	/**
	 * 数据缓冲处理
	 * @param key
	 * @param timeUnit
	 * @param length
	 * @param valueType
	 * @param bufferedValueMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<Number> getValueByBuffered(String key, TimeUnit timeUnit, Integer length, Class<?> valueType){
		//TODO 缺缓冲实现
		return (List<Number>)counterContainer.window(key, timeUnit, length, valueType);
	}
	
	/**
	 * 获取逻辑主体的数据处理
	 * @param logicSubscriber
	 * @return
	 */
	protected List<Number> getLogicValue(ILogicSubscriber logicSubscriber){
		long old = System.currentTimeMillis();
		List<Number> valueList = new ArrayList<Number>();
		try{
			
			List<Number> min = null;
			TimeUnit timeUnit = logicSubscriber.getTimeUnit();
			Integer length = logicSubscriber.getLength();
			Class<?> valueType = logicSubscriber.getValueType();
			Map<String, String> variableMap = logicSubscriber.getTopicVariableMap();
			Map<String, List<Number>> dataMap = new HashMap<String, List<Number>>();
			Map<String, Topic> topicMap = new HashMap<String, Topic>();
			
			for(Entry<String, String> entry:variableMap.entrySet()){
				Topic topic = topicContainer.getTopic(entry.getKey());
				topicMap.put(entry.getKey(), topic);
				//找出最大的时间单位(小的兼容大的时间单位)
				if(timeUnit==null || timeUnit.toMillis(1) < topic.getTimeUnit().toMillis(1)){
					timeUnit = topic.getTimeUnit();
				}
			}
			//读取所有需要参与计算的数据，并找出最少的的数据集，以最小数据集的长度为逻辑数据集的长度
			for(Entry<String, String> entry:variableMap.entrySet()){
				List<Number> temp = this.getValueByBuffered(entry.getKey(), timeUnit, length, valueType);
				if(temp==null){
					//如果其中有一个数据集是空的，那么返回空的
					return valueList;
				}
				if(min==null||temp.size()<min.size()){
					min = temp;
				}
				dataMap.put(entry.getKey(), temp);
			}
			//计算逻辑的
			for(int i=0;i<min.size();i++){
				StandardEvaluationContext context = new StandardEvaluationContext();
				for(Entry<String, String> entry:variableMap.entrySet()){
					context.setVariable(entry.getValue(), dataMap.get(entry.getKey()).get(i));
				}
				Number value = 0;
				try{
					value = (Number) this.parseExpression(context, logicSubscriber.getTopicMatch(), valueType);
				}catch(Exception e){
					log.error(e.getMessage(), e);
				}
				valueList.add(value);
			}
		}catch(Exception e){
			log.error(e.getMessage(), e);
			
		}finally {

			if(log.isTraceEnabled()){
				log.trace(String.format("get logic topic value , cost time is %s", System.currentTimeMillis() - old));
			}
		}
		
		return valueList;
	}
	
	
	/**
	 * 执行任务
	 * @param event
	 * @param subscriber
	 */
	protected void execute(Long eventTime, IChannel channel){
		
		executor.execute(new Runnable(){
			@Override
			public void run() {
				long old = System.currentTimeMillis();
				
				ISubscriber subscriber = channel.getSubscriber();
				String key = subscriber.getTopicMatch();
				try{
					if(log.isDebugEnabled()){
						log.debug(String.format("call notice of subscriber[%s] start", key));
					}
					INoticeEvent<?> event = parseValue(channel, eventTime);
					
					//是否做预警判断
					if(subscriber instanceof IWarnSubscriber){
						IWarnSubscriber wsub = (IWarnSubscriber) subscriber;
						StandardEvaluationContext context = getContextByEvent(event);
						boolean result = parseExpression(context, wsub.getCondition() , Boolean.class);
						if(!result){
							return ;
						}
					}
					
					subscriber.getNotice().notice(event);
				}catch(Exception e){
					log.error(e.getMessage(), e);
				}finally{
					long diff = System.currentTimeMillis() - old;
					if(log.isDebugEnabled()){
						log.debug(String.format("call notice of subscriber[%s] end, cost time is %sms", key, diff));
					}
				}
			}
			
		});
	}
	
	/**
	 * 计数心跳
	 */
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
	public IDispatcher createCounter(Topic topic) {
		this.createQueueTask(new Runnable() {
			@Override
			public void run() {
				if(log.isDebugEnabled()){
					log.debug(String.format("create counter %s", topic.getKey()));
				}
				counterContainer.createCounter(topic);
				topicContainer.createTopic(topic);
				channelContainer.put(topic.getKey());
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
	
	/**
	 * 获取函数spel对应初始化的上下文
	 * @param joinPoint
	 * @return
	 */
	protected StandardEvaluationContext getContextByEvent(INoticeEvent<?> event){
		
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("event", event);
		if(event instanceof INoticeStatisticsEvent){
			INoticeStatisticsEvent<?> sevent = (INoticeStatisticsEvent<?>) event;
			context.setVariable("max", sevent.getMax());
			context.setVariable("min", sevent.getMin());
			context.setVariable("sum", sevent.getSum());
			context.setVariable("avg", sevent.getAvg());			
			context.setVariable("cur", sevent.getCur());
		}

		return context;
	}
	
	/**
	 * spel格式化
	 * @param context
	 * @param str
	 * @return
	 */
	protected <T> T parseExpression(StandardEvaluationContext context,String str, Class<T> clazz){
		return new SpelExpressionParser().parseExpression(str).getValue(context, clazz);
	}

	@Override
	public IDispatcher unsubscribe(ISubscriber subscriber) {
		channelContainer.delete(subscriber);
		return this;
	}

}

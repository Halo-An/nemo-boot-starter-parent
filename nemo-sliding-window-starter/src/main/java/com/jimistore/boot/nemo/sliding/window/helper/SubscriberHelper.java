package com.jimistore.boot.nemo.sliding.window.helper;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.jimistore.boot.nemo.sliding.window.annotation.Subscribe;
import com.jimistore.boot.nemo.sliding.window.core.INotice;
import com.jimistore.boot.nemo.sliding.window.core.INoticeEvent;
import com.jimistore.boot.nemo.sliding.window.core.INoticeStatisticsEvent;
import com.jimistore.boot.nemo.sliding.window.core.ISubscriber;
import com.jimistore.boot.nemo.sliding.window.core.NoticeStatisticsEvent;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;

public class SubscriberHelper {
	
	SlidingWindowTemplate slidingWindowTemplate;
	
	public SubscriberHelper setSlidingWindowTemplate(SlidingWindowTemplate slidingWindowTemplate) {
		this.slidingWindowTemplate = slidingWindowTemplate;
		return this;
	}
	
	public void createSubscriber(Subscribe subscribe, Method method, Object invoker){
		slidingWindowTemplate.subscribe(new ISubscriber(){

			@Override
			public String getTopicMatch() {
				return subscribe.value();
			}

			@Override
			public Integer getLength() {
				return subscribe.length();
			}

			@Override
			public TimeUnit getTimeUnit() {
				return subscribe.timeUnit();
			}

			@Override
			public Integer getInterval() {
				return subscribe.interval();
			}

			@Override
			public INotice getNotice() {
				return new INotice(){
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void notice(INoticeEvent<?> event) {   
						Class<?>[] types = method.getParameterTypes();
						Object[] params = new Object[types.length];
						for(int i=0;i<types.length;i++){
							if(INoticeStatisticsEvent.class.isAssignableFrom(types[i])){
								params[i] = new NoticeStatisticsEvent(event);
							}else if(INoticeEvent.class.isAssignableFrom(types[i])){
								params[i] = event;
							}else{
								params[i]=null;
							}
						}
						
						try {
							method.invoke(invoker, params);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
						
					}
				};
			}
			
			
		});
	}

}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;

import com.jimistore.boot.nemo.sliding.window.helper.NumberUtil;

public class NoticeStatisticsEvent<T extends Number> implements INoticeStatisticsEvent<T> {
	
	private INoticeEvent<T> noticeEvent;
	
	private T min,max,sum,avg;
	
	@SuppressWarnings("unchecked")
	public NoticeStatisticsEvent(INoticeEvent<T> noticeEvent){
		this.noticeEvent = noticeEvent;
		List<T> list = noticeEvent.getValue();
		for(T t:list){
			if(min==null){
				min=t;
				max=t;
				sum=t;
			}else{

				if(NumberUtil.compare(min, t)>0){
					min=t;
				}
				if(NumberUtil.compare(max, t)<0){
					max=t;
				}
				sum = (T) NumberUtil.add(sum, t);
			}
		}
		avg = (T) NumberUtil.except(sum, list.size());
	}

	@Override
	public String getTopicKey() {
		
		return noticeEvent.getTopicKey();
	}

	@Override
	public Long getTime() {
		
		return noticeEvent.getTime();
	}

	@Override
	public List<T> getValue() {
		
		return noticeEvent.getValue();
	}

	@Override
	public T getMin() {
		// TODO Auto-generated method stub
		return min;
	}

	@Override
	public T getMax() {
		// TODO Auto-generated method stub
		return max;
	}

	@Override
	public T getSum() {
		// TODO Auto-generated method stub
		return sum;
	}

	@Override
	public T getAvg() {
		// TODO Auto-generated method stub
		return avg;
	}
	
	 

}

package com.jimistore.boot.nemo.sliding.window.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.jimistore.boot.nemo.sliding.window.exception.ValidateException;
import com.jimistore.boot.nemo.sliding.window.helper.NumberUtil;

/**
 * 计数器
 * @author chenqi
 * @Date 2018年6月8日
 *
 * @param <T>
 */
public class Counter<T> implements ICounter<T> {
	
	public static final Long START_KEY = -1l;
	
	private TimeUnit timeUnit;
	
	private int capacity;
	
	private String key;
	
	private long start;
	
	protected Map<Long, Number> valueMap = new HashMap<Long, Number>();
	
	private long nextHeartbeatTime=0;
	
	protected Counter(){
	}
	
	public static <E> Counter<E> create(String key, TimeUnit timeUnit, Integer capacity, Class<E> valueType){
		return new Counter<E>().checkType(valueType).setCapacity(capacity).setTimeUnit(timeUnit).setStart(System.currentTimeMillis());
	}
	
	protected Counter<T> setStart(long start){
		this.start = start;
		valueMap.put(START_KEY, start);
		return this;
	}


	@Override
	public void heartbeat() {
		long now = System.currentTimeMillis();
		if(now >= nextHeartbeatTime){
			nextHeartbeatTime = now + timeUnit.toMillis(1);
			
			Long index = this.getIndex(valueMap.get(START_KEY).longValue())-capacity;
			if(valueMap.containsKey(index)){
				valueMap.remove(index);
			}
		}
		
	}
	
	protected Long getIndex(long start){
		long now = System.currentTimeMillis();
		Long index = (now / timeUnit.toMillis(1) - start / timeUnit.toMillis(1)) % capacity;
		return index;
	}

	@Override
	public Counter<T> put(IPublishEvent<?> event) {
		
		Long index = this.getIndex(valueMap.get(START_KEY).longValue());
		Number value = valueMap.get(index);
		valueMap.put(index, NumberUtil.add(value, event.getValue()));
		
		return this;
	}

	public long getStart() {
		return start;
	}

	@Override
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType) {
		return this.window(this.valueMap, timeUnit, length, valueType);
	}
	
	@SuppressWarnings("unchecked")
	protected <E> List<E> window(Map<Long, Number> dataMap, TimeUnit timeUnit, Integer length, Class<E> valueType) {
		this.checkType(valueType);
		if(dataMap==null||dataMap.size()==0){
			throw new ValidateException(String.format("dataMap[%s] can not be empty", key));
		}
		
		long times = timeUnit.toMillis(1) / this.timeUnit.toMillis(1);
		List<E> dataList = new ArrayList<E>();
		long index = this.getIndex(dataMap.get(START_KEY).longValue());
		long cursor = index;
		for(int i=0;i<length;i++){
			Number value = 0;
			for(int j=0;j<times;j++){
				cursor--;
				if(cursor<0){
					cursor = capacity - 1;
				}
				//如果已经覆盖整个计数器，则跳出循环
				if(cursor==index){
					break;
				}
				value = NumberUtil.add(value, dataMap.get(cursor));
				if(value==null){
					value=0;
				}
			}
			//如果已经覆盖整个计数器，则跳出循环
			if(cursor==index){
				break;
			}
			
			dataList.add((E)value);
		}
		
		return dataList;
	}

	public Counter<T> setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		return this;
	}

	public Counter<T> setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}
	
	public Counter<T> setKey(String key) {
		this.key = key;
		return this;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getKey() {
		return key;
	}

	/**
	 * 检验数据类型是否支持
	 * @param valueType
	 */
	protected Counter<T> checkType(Class<?> valueType){
		NumberUtil.checkType(valueType);
		return this;
	}

}

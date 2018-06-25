package com.jimistore.boot.nemo.sliding.window.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.jimistore.boot.nemo.sliding.window.exception.TypeCannotSupportException;
import com.jimistore.boot.nemo.sliding.window.helper.NumberUtil;

/**
 * 计数器
 * @author chenqi
 * @Date 2018年6月8日
 *
 * @param <T>
 */
public class OldCounter<T> extends Thread implements ICounter<T> {
	
	/**
	 * 上个索引的时间(微秒)
	 */
	public Long old;
	
	/**
	 * 上个索引的值
	 */
	public Integer index=0;
	
	TimeUnit timeUnit;
	
	int capacity;
	
	String key;
	
	protected Map<Integer, Number> dataMap = new HashMap<Integer, Number>();
	
	protected OldCounter(){
	}
	
	public static <E> OldCounter<E> create(String key, TimeUnit timeUnit, Integer capacity, Class<E> valueType){
		return new OldCounter<E>().checkType(valueType).setCapacity(capacity).setTimeUnit(timeUnit).init();
	}
	
	private OldCounter<T> init(){
		for(int i=0;i<capacity;i++){
			dataMap.put(i, 0);
		}
		old = System.currentTimeMillis();
		return this;
	}

	@Override
	public OldCounter<T> put(IPublishEvent<?> event) {
		this.handdleDiff();
		Number value = dataMap.get(index);
		dataMap.put(index, NumberUtil.add(value, event.getValue()));
		
		
		return this;
	}
	
	/**
	 * 处理偏移
	 * @return
	 */
	protected synchronized OldCounter<T> handdleDiff(){
		
		Long now = System.currentTimeMillis();
		long diff = now / timeUnit.toMillis(1) - old / timeUnit.toMillis(1);
		for(;diff>0;diff--){
			index++;
			if(index>=capacity){
				index = 0;
			}
			dataMap.put(index, 0);
			old = now;
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> List<E> window(TimeUnit timeUnit, Integer length, Class<E> valueType) {
		this.checkType(valueType);
		this.handdleDiff();
		
		long times = timeUnit.toMillis(1) / this.timeUnit.toMillis(1);
		List<E> dataList = new ArrayList<E>();
		int cursor = index;
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
			}
			//如果已经覆盖整个计数器，则跳出循环
			if(cursor==index){
				break;
			}
			dataList.add((E)value);
		}
		
		return dataList;
	}

	public OldCounter<T> setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
		return this;
	}

	public OldCounter<T> setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}
	
	public OldCounter<T> setKey(String key) {
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
	protected OldCounter<T> checkType(Class<?> valueType){
		if(valueType.equals(Double.class)){
			return this;
		}
		if(valueType.equals(Float.class)){
			return this;
		}if(valueType.equals(Long.class)){
			return this;
		}if(valueType.equals(Integer.class)){
			return this;
		}if(valueType.equals(Short.class)){
			return this;
		}
		throw new TypeCannotSupportException();
	}

}

package com.jimistore.boot.nemo.high.concurrency.helper;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;

import com.cq.nemo.core.exception.StockConsumeException;

public class StockHelper {
	
	private static final Logger log = Logger.getLogger(StockHelper.class);
	
	private String suffix = ".stock";
	
	private String maxSuffix = ".maxstock";
	
	RedisTemplate<String,Long> redisTemplate;
	
	public StockHelper setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	public StockHelper setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public void setMaxSuffix(String maxSuffix) {
		this.maxSuffix = maxSuffix;
	}

	public StockHelper cover(String key,Long count){
		log.debug(String.format("request cover , the key is : %s, the num is : %s", key, count));
		//设置新的最大库存并获取老的最大库存
		Long oldMaxStock = parseLong(redisTemplate.opsForValue().getAndSet(String.format("%s%s", key, maxSuffix), count));
		if(oldMaxStock==null||oldMaxStock==0){
			//如果老的库存没有设置，则这是一个新的库存，则创建一个库存
			return this.create(key, count);
		}
		
		if(oldMaxStock<count){
			//如果新的库存值大于老的库存值，则新增库存
			return this.produce(key, count-oldMaxStock);
		}
		
		if(oldMaxStock>count){
			//如果新的库存值小于老的库存值，则减少库存
			redisTemplate.opsForValue().increment(getFormatKey(key), count-oldMaxStock);
			return this;
		}
		
		//如果新老库存相等，不做任何处理
		return this;
	}
	
	public StockHelper create(String key,Long count){
		log.debug(String.format("request create , the key is : %s, the num is : %s", key, count));
		//设置最大库存
		redisTemplate.opsForValue().getAndSet(String.format("%s%s", key, maxSuffix), count);
		//设置即时库存
		redisTemplate.opsForValue().getAndSet(getFormatKey(key), count);
		return this;
	}

	public StockHelper produce(String key,Long count){
		log.debug(String.format("request produce , the key is : %s, the num is : %s", key, count));
		//新增库存
		Long newStock = parseLong(redisTemplate.opsForValue().increment(getFormatKey(key), count));
		//如果老库存已亏损,小于0,则补充亏损
		if(newStock<0){
			redisTemplate.opsForValue().getAndSet(getFormatKey(key), count);
		}else if(newStock<count){
			this.produce(key, count-newStock);
		}
		return this;
	}
	
	public StockHelper consume(String key, Long num, String prompt) throws StockConsumeException{
		log.debug(String.format("request consume , the key is : %s, the num is : %s", key, num));
		
		Long newStock = parseLong(redisTemplate.opsForValue().increment(getFormatKey(key), -1*num));
		//如果库存小于0
		if(newStock<0){
			throw new StockConsumeException(prompt);
		}
		return this;
	}
	
	private String getFormatKey(String key){
		return String.format("%s%s", key, suffix);
	}
	
	private Long parseLong(Object obj){
		if(obj==null){
			return null;
		}
		Long longValue = null;
		if(obj instanceof Integer){
			longValue = ((Integer)obj).longValue();
		}else if(obj instanceof Long){
			longValue = (Long)obj;
		}
		return longValue;
	}

}

package com.jimistore.boot.nemo.sliding.window.core;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * 计数器容器
 * @author chenqi
 * @Date 2018年6月1日
 *
 */
public class RedisCounterContainer extends LocalCounterContainer implements ICounterContainer {
	
	RedisTemplate<?,?> redisTemplate;

	public RedisCounterContainer setRedisTemplate(RedisTemplate<?,?> redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}
	
}
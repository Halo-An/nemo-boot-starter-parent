package com.jimistore.boot.nemo.id.generator.helper;

import org.springframework.data.redis.core.RedisTemplate;

import com.jimistore.boot.nemo.id.generator.annotation.IDGenerator;
import com.jimistore.boot.nemo.id.generator.core.IIDGenerator;

@SuppressWarnings("rawtypes")
public class IDGeneratorHelper {
	
	RedisTemplate redisTemplate;
	

	public IDGeneratorHelper setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}
	
	public String generator(IDGenerator anno) {
		long num = this.generatorNum(anno.value(), anno.start());
		
		try {
			IIDGenerator iDGenerator = (IIDGenerator) anno.generatorClass().newInstance();
			return iDGenerator.generator(anno.sequence(), anno.length(), num);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public long generatorNum(String key, long start) {
		long num = redisTemplate.opsForValue().increment(key, 1);
		if(num <= start) {
			num = redisTemplate.opsForValue().increment(key, start);
		}
		return num;
	}
	

}

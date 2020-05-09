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
		return this.generator(anno.value(), anno.start(), anno.sequence(), anno.length(), anno.generatorClass());
	}

	/**
	 * 生成序列号
	 * 
	 * @param key      标识
	 * @param start    开始索引
	 * @param sequence 生成序列号的字符序列
	 * @param length   长度
	 * @param clazz
	 * @return
	 */
	public String generator(String key, long start, String sequence, int length, Class<?> clazz) {
		long index = this.generatorNum(key, start);
		try {
			IIDGenerator iDGenerator = (IIDGenerator) clazz.newInstance();
			return iDGenerator.generator(sequence, length, index);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 生成序列号
	 * 
	 * @param key      标识
	 * @param start    开始索引
	 * @param sequence 生成序列号的字符序列
	 * @param length   长度
	 * @return
	 */
	public String generator(String key, long start, String sequence, int length) {
		return this.generator(key, start, sequence, length,
				com.jimistore.boot.nemo.id.generator.core.IDGenerator.class);
	}

	@SuppressWarnings("unchecked")
	public long generatorNum(String key, long start) {
		long num = redisTemplate.opsForValue().increment(key, 1);
		if (num <= start) {
			num = redisTemplate.opsForValue().increment(key, (start - num + 1));
		}
		return num;
	}

}

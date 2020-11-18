package com.jimistore.boot.nemo.lock.helper;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import com.jimistore.boot.nemo.lock.exception.StockConsumeException;

public class StockHelper {

	private static final Logger LOG = LoggerFactory.getLogger(StockHelper.class);

	private String suffix = ".stock";

	private String maxSuffix = ".maxstock";

	RedisTemplate<String, Long> redisTemplate;

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

	public StockHelper cover(String key, Long count, Long timeout) {
		LOG.debug(String.format("request cover , the key is : %s, the num is : %s", key, count));
		// 设置新的最大库存并获取老的最大库存
		Long oldMaxStock = parseLong(
				redisTemplate.opsForValue().getAndSet(String.format("%s%s", key, maxSuffix), count));
		redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().count(1000).match("").build());
		if (oldMaxStock == null || oldMaxStock == 0) {
			// 如果老的库存没有设置，则这是一个新的库存，则创建一个库存
			return this.create(key, count, timeout);
		}

		if (oldMaxStock < count) {
			// 如果新的库存值大于老的库存值，则新增库存
			return this.produce(key, count - oldMaxStock);
		}

		if (oldMaxStock > count) {
			// 如果新的库存值小于老的库存值，则减少库存
			redisTemplate.opsForValue().increment(getFormatKey(key), count - oldMaxStock);
			return this;
		}

		// 如果新老库存相等，不做任何处理
		return this;
	}

	public StockHelper create(String key, Long count, Long timeout) {
		LOG.debug(String.format("request create , the key is : %s, the num is : %s", key, count));
		// 设置最大库存
		redisTemplate.opsForValue().getAndSet(String.format("%s%s", key, maxSuffix), count);
		// 设置即时库存
		redisTemplate.opsForValue().getAndSet(getFormatKey(key), count);

		// 设置过期时间
		if (timeout == null || timeout <= 0) {
			redisTemplate.expire(String.format("%s%s", key, maxSuffix), timeout, TimeUnit.MILLISECONDS);
			redisTemplate.expire(getFormatKey(key), timeout, TimeUnit.MILLISECONDS);
		}
		return this;
	}

	public StockHelper produce(String key, Long count) {
		LOG.debug(String.format("request produce , the key is : %s, the num is : %s", key, count));
		// 新增库存
		Long newStock = parseLong(redisTemplate.opsForValue().increment(getFormatKey(key), count));
		// 如果老库存已亏损,小于0,则补充亏损
		if (newStock < 0) {
			redisTemplate.opsForValue().getAndSet(getFormatKey(key), count);
		} else if (newStock < count) {
			this.produce(key, count - newStock);
		}
		return this;
	}

	public StockHelper consume(String key, Long num, String prompt) throws StockConsumeException {
		LOG.debug(String.format("request consume , the key is : %s, the num is : %s", key, num));

		Long newStock = parseLong(redisTemplate.opsForValue().increment(getFormatKey(key), -1 * num));
		// 如果库存小于0
		if (newStock < 0) {
			throw new StockConsumeException(prompt);
		}
		return this;
	}

	private String getFormatKey(String key) {
		return String.format("%s%s", key, suffix);
	}

	private Long parseLong(Object obj) {
		if (obj == null) {
			return null;
		}
		Long longValue = null;
		if (obj instanceof Integer) {
			longValue = ((Integer) obj).longValue();
		} else if (obj instanceof Long) {
			longValue = (Long) obj;
		}
		return longValue;
	}

}

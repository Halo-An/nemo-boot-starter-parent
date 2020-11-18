package com.jimistore.boot.nemo.lock.helper;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.jimistore.boot.nemo.lock.exception.LockException;

public class LockHelper {

	private static final Logger LOG = LoggerFactory.getLogger(LockHelper.class);

	private static final String suffix = ".lock";

	RedisTemplate<String, Long> redisTemplate;

	public LockHelper setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
		this.redisTemplate = redisTemplate;
		return this;
	}

	public LockHelper lock(String keyStr, long timeout, String prompt) throws LockException {
		LOG.debug(String.format("request lock , the key is : %s", keyStr));
		String key = String.format("%s%s", keyStr, suffix);
		long time = Calendar.getInstance().getTimeInMillis();
		try {
			boolean result = redisTemplate.opsForValue().setIfAbsent(key, time);
			if (!result) {
				// 如果已经加过锁,则判断加锁是否已经失效
				long old = redisTemplate.opsForValue().get(key);
				if (time - old > timeout) {
					// 如果加锁已经失效,则加锁
					long temp = redisTemplate.opsForValue().getAndSet(key, time);
					// 判断上一次的锁是否就是失效的锁
					if (temp == old) {
						// 如果是则加锁成功
						return this;
					} else {
						// 如果不是则加锁异常
					}
				} else {
					// 如果未失效则返回异常
				}
				throw new LockException(prompt);
			}
		} finally {
			redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
		}
		// 如果没有加过锁,则加锁
		return this;
	}

	public LockHelper unlock(String keyStr) {
		LOG.debug(String.format("request unlock , the key is : %s", keyStr));
		String key = String.format("%s%s", keyStr, suffix);
		// 移除锁
		redisTemplate.delete(key);
		return this;
	}

	public LockHelper unlock(String subject, String operator) {
		return this.unlock(String.format("%s.%s", operator, subject));
	}

	public LockHelper lock(String subject, String operator, long timeout, String prompt) throws LockException {
		return this.lock(String.format("%s.%s", operator, subject), timeout, prompt);
	}

}

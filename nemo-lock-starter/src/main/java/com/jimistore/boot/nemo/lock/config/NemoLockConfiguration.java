package com.jimistore.boot.nemo.lock.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.lock.helper.AsyncExecuterAspect;
import com.jimistore.boot.nemo.lock.helper.AsyncExecuterHelper;
import com.jimistore.boot.nemo.lock.helper.LockAspect;
import com.jimistore.boot.nemo.lock.helper.LockHelper;
import com.jimistore.boot.nemo.lock.helper.StockAspect;
import com.jimistore.boot.nemo.lock.helper.StockHelper;

@Configuration
public class NemoLockConfiguration {
	
	@Bean("redisTemplate")
	@ConditionalOnMissingBean(value = RedisTemplate.class)
	public RedisTemplate<String, String> redisTemplate(
			RedisConnectionFactory connectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
		Jackson2JsonRedisSerializer<?> nemoJsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		nemoJsonRedisSerializer.setObjectMapper(om);
		template.setKeySerializer(nemoJsonRedisSerializer);
		template.setValueSerializer(nemoJsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public LockHelper lockHelper(RedisTemplate redisTemplate){
		return new LockHelper().setRedisTemplate(redisTemplate);
	}
	
	@Bean
	public LockAspect lockAspect(LockHelper lockHelper){
		return new LockAspect().setLockHelper(lockHelper);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	@ConditionalOnMissingBean(value=StockHelper.class)
	public StockHelper stockHelper(RedisTemplate redisTemplate){
		return new StockHelper().setRedisTemplate(redisTemplate);
	}
	
	@Bean
	@ConditionalOnMissingBean(value=StockAspect.class)
	public StockAspect StockAspect(StockHelper stockHelper){
		return new StockAspect().setStockHelper(stockHelper);
	}
	
	@Bean
	@ConditionalOnMissingBean(value=AsyncExecuterHelper.class)
	public AsyncExecuterHelper AsyncExecuterHelper(){
		return new AsyncExecuterHelper();
	}
	
	@Bean
	@ConditionalOnMissingBean(value=AsyncExecuterAspect.class)
	public AsyncExecuterAspect AsyncExecuterAspect(AsyncExecuterHelper asyncExecuterHelper){
		return new AsyncExecuterAspect().setAsyncExecuterHelper(asyncExecuterHelper);
	}
	
}

package com.jimistore.boot.nemo.sliding.window.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;
import com.jimistore.boot.nemo.sliding.window.helper.PublishAspect;
import com.jimistore.boot.nemo.sliding.window.helper.PublisherHelper;
import com.jimistore.boot.nemo.sliding.window.helper.SlidingWindowClient;
import com.jimistore.boot.nemo.sliding.window.helper.SubscriberHelper;

@Configuration
@EnableConfigurationProperties(SlidingWindowProperties.class)
public class NemoSlidingWindowConfiguration {

	@Bean("redisTemplate")
	@ConditionalOnMissingBean(value = RedisTemplate.class)
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
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

	@Bean("objectMapper")
	@ConditionalOnMissingBean(value = ObjectMapper.class)
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	@ConditionalOnMissingBean(SlidingWindowTemplate.class)
	public SlidingWindowTemplate slidingWindowTemplate(SlidingWindowProperties slidingWindowProperties,
			@Lazy RedisTemplate<String, String> redisTemplate, @Lazy ObjectMapper objectMapper) {
		return SlidingWindowTemplate.create(slidingWindowProperties, redisTemplate, objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean(PublisherHelper.class)
	public PublisherHelper publisherHelper(SlidingWindowTemplate slidingWindowTemplate, @Value("${spring.application.group}") String service) {
		return new PublisherHelper().setSlidingWindowTemplate(slidingWindowTemplate).setService(service);
	}

	@Bean
	@ConditionalOnMissingBean(SubscriberHelper.class)
	public SubscriberHelper subscriberHelper(SlidingWindowTemplate slidingWindowTemplate) {
		return new SubscriberHelper().setSlidingWindowTemplate(slidingWindowTemplate);
	}

	@Bean
	@ConditionalOnMissingBean(PublishAspect.class)
	public PublishAspect publishAspect(PublisherHelper publisherHelper) {
		return new PublishAspect().setPublisherHelper(publisherHelper);
	}

	@Bean
	@ConditionalOnMissingBean(SlidingWindowClient.class)
	public SlidingWindowClient slidingWindowClient(@Lazy PublisherHelper publisherHelper,
			@Lazy SubscriberHelper subscriberHelper) {
		return new SlidingWindowClient().setPublisherHelper(publisherHelper).setSubscriberHelper(subscriberHelper);
	}

}

package com.jimistore.boot.nemo.core.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.MessageInterpolator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.core.api.service.OfflineHandler;
import com.jimistore.boot.nemo.core.api.service.OnlineHandler;
import com.jimistore.boot.nemo.core.helper.CharsetFilter;
import com.jimistore.boot.nemo.core.helper.CorsFilter;
import com.jimistore.boot.nemo.core.helper.DaoLoggerAspect;
import com.jimistore.boot.nemo.core.helper.InitContextFilter;
import com.jimistore.boot.nemo.core.helper.NemoJsonKeyGennerator;
import com.jimistore.boot.nemo.core.helper.NemoJsonRedisSerializer;
import com.jimistore.boot.nemo.core.helper.NemoMethodValidationPostProcessor;
import com.jimistore.boot.nemo.core.helper.OfflineRequestHandler;
import com.jimistore.boot.nemo.core.helper.OnlineRequestHandler;
import com.jimistore.boot.nemo.core.helper.RequestLoggerAspect;
import com.jimistore.boot.nemo.core.helper.RequestProxyFilter;
import com.jimistore.boot.nemo.core.helper.ResponseBodyWrapFactory;
import com.jimistore.boot.nemo.core.helper.ResponseExceptionHandle;
import com.jimistore.boot.nemo.core.helper.ServiceLoggerAspect;

@Configuration
@EnableCaching
@EnableConfigurationProperties(RedisProperties.class)
public class NemoCoreAutoConfiguration {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
				configurer.defaultContentType(MediaType.APPLICATION_JSON);
			}

		};
	}

	@Bean
	public ResponseBodyWrapFactory responseBodyWrapFactory() {
		return new ResponseBodyWrapFactory();
	}

	@Bean
	public ResponseExceptionHandle responseExceptionHandle() {
		return new ResponseExceptionHandle();
	}

	@Bean
	public InitContextFilter initContextFilter() {
		return new InitContextFilter();
	}

	@Bean
	public RequestProxyFilter requestProxyFilter() {
		return new RequestProxyFilter();
	}

	@Bean
	public CharsetFilter charsetFilter() {
		return new CharsetFilter();
	}

	@Bean
	public CorsFilter corsFilter() {
		return new CorsFilter();
	}

	@Bean
	public RequestLoggerAspect requestLoggerFilter() {
		return new RequestLoggerAspect();
	}

	@Bean
	public ServiceLoggerAspect serviceLoggerAspect() {
		return new ServiceLoggerAspect();
	}

	@Bean
	public DaoLoggerAspect daoLoggerAspect() {
		return new DaoLoggerAspect();
	}

	@Configuration
	@ConditionalOnClass(MessageInterpolator.class)
	class ValidateConfiguration {

		@Bean
		@ConditionalOnClass(MessageInterpolator.class)
		public MessageInterpolator messageInterpolator() {
			return new MessageInterpolator() {

				@Override
				public String interpolate(String messageTemplate, Context context) {
					return null;
				}

				@Override
				public String interpolate(String messageTemplate, Context context, Locale locale) {

					if (messageTemplate.indexOf("NotNull") >= 0 || messageTemplate.indexOf("NotBlank") >= 0) {
						return " ${field} cannot be empty. ";
					}
					if (messageTemplate.indexOf("{") >= 0) {
						return " ${field} format error. ";
					}
					return messageTemplate;
				}

			};
		}

		@Bean
		@ConditionalOnClass(MessageInterpolator.class)
		public LocalValidatorFactoryBean localValidatorFactoryBean(MessageInterpolator messageInterpolator) {
			LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
			localValidatorFactoryBean.setMessageInterpolator(messageInterpolator);
			return localValidatorFactoryBean;
		}

		@Bean
		@ConditionalOnClass(MessageInterpolator.class)
		public MethodValidationPostProcessor methodValidationPostProcessor(
				LocalValidatorFactoryBean localValidatorFactoryBean) {
			NemoMethodValidationPostProcessor methodValidationPostProcessor = new NemoMethodValidationPostProcessor();
			methodValidationPostProcessor.setValidatorFactory(localValidatorFactoryBean);
			methodValidationPostProcessor.setOrder(1);
			return methodValidationPostProcessor;
		}
	}

	@Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate redisTemplate(RedisConnectionFactory connectionFactory) {

		StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
		NemoJsonRedisSerializer nemoJsonRedisSerializer = new NemoJsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		nemoJsonRedisSerializer.setObjectMapper(om);
		template.setKeySerializer(nemoJsonRedisSerializer);
		template.setValueSerializer(nemoJsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisProperties redisProperties, RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration defaultRedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(300));

		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		cacheConfigurations.put("day", defaultRedisCacheConfiguration.entryTtl(Duration.ofSeconds(86400)));
		cacheConfigurations.put("list", defaultRedisCacheConfiguration.entryTtl(Duration.ofSeconds(1)));
		if (redisProperties != null && redisProperties.getExpired() != null) {
			Iterator<Entry<String, Long>> it = redisProperties.getExpired().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Long> entry = it.next();
				cacheConfigurations.put(entry.getKey(),
						defaultRedisCacheConfiguration.entryTtl(Duration.ofSeconds(entry.getValue())));
			}
		}

		return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
				.cacheDefaults(defaultRedisCacheConfiguration)
				.withInitialCacheConfigurations(cacheConfigurations)
				.build();

	}

	@Bean
	public NemoJsonKeyGennerator keyGenerator() {
		return new NemoJsonKeyGennerator();
	}

	@Bean("/offline")
	public OfflineRequestHandler OfflineRequestHandler(@Lazy Set<OfflineHandler> offlineSet) {
		return new OfflineRequestHandler().setOfflineSet(offlineSet);
	}

	@Bean("/online")
	public OnlineRequestHandler OnlineRequestHandler(@Lazy Set<OnlineHandler> onlineSet) {
		return new OnlineRequestHandler().setOnlineSet(onlineSet);
	}

}

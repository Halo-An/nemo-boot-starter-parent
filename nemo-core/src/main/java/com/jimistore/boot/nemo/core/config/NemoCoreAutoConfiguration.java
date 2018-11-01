package com.jimistore.boot.nemo.core.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.MessageInterpolator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.core.helper.InitContextAspect;
import com.jimistore.boot.nemo.core.helper.NemoJsonRedisSerializer;
import com.jimistore.boot.nemo.core.helper.NemoMethodValidationPostProcessor;
import com.jimistore.boot.nemo.core.helper.RequestLoggerAspect;
import com.jimistore.boot.nemo.core.helper.ResponseBodyWrapFactory;
import com.jimistore.boot.nemo.core.helper.ResponseExceptionHandle;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class NemoCoreAutoConfiguration {

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**");
                registry.addMapping("/meta/**");
            }
        };
    }
	
	@Bean
	public InitContextAspect InitContextAspect(){
		return new InitContextAspect();
	}
	

	
	@Bean
	public ResponseBodyWrapFactory responseBodyWrapFactory(){
		return new ResponseBodyWrapFactory();
	}


	@Bean
	public ResponseExceptionHandle responseExceptionHandle(){
		return new ResponseExceptionHandle();
	}
	
	@Bean
	public RequestLoggerAspect requestLoggerAspect(){
		return new RequestLoggerAspect();
	}
	
	
	
	@Bean
	public MessageInterpolator messageInterpolator(){
		return new MessageInterpolator(){

			@Override
			public String interpolate(String messageTemplate, Context context) {
				return null;
			}

			@Override
			public String interpolate(String messageTemplate, Context context,
					Locale locale) {
				
				if(messageTemplate.indexOf("NotNull")>=0 || messageTemplate.indexOf("NotBlank")>=0){
					return " ${field} cannot be empty. ";
				}
				if(messageTemplate.indexOf("{")>=0){
					return " ${field} format error. ";
				}
				return messageTemplate;
			}
			
		};
	}

	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean(MessageInterpolator messageInterpolator) {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setMessageInterpolator(messageInterpolator);
		return localValidatorFactoryBean;
	}
	
	
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean localValidatorFactoryBean){
		NemoMethodValidationPostProcessor methodValidationPostProcessor =new NemoMethodValidationPostProcessor();
		methodValidationPostProcessor.setValidatorFactory(localValidatorFactoryBean);
		methodValidationPostProcessor.setOrder(1);
		return methodValidationPostProcessor;
	}

	@Bean
	@ConditionalOnMissingBean(RedisTemplate.class)
	public RedisTemplate<String, String> redisTemplate(
			RedisConnectionFactory connectionFactory) {
		
		StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
		NemoJsonRedisSerializer nemoJsonRedisSerializer = new NemoJsonRedisSerializer(
				Object.class);
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
	
	@Bean
	public CacheManager cacheManager(
			@SuppressWarnings("rawtypes") RedisTemplate redisTemplate, RedisProperties redisProperties) {
		RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
		redisCacheManager.setDefaultExpiration(300l);
		Map<String,Long> map=new HashMap<String,Long>();
		map.put("day", 86400l);
		map.put("default", 300l);
		map.put("list", 1l);
		if(redisProperties!=null&&redisProperties.getExpired()!=null){
			Iterator<Entry<String, Long>> it = redisProperties.getExpired().entrySet().iterator();
			while(it.hasNext()){
				Entry<String, Long> entry = it.next();
				map.put(entry.getKey(), entry.getValue());
			}
		}
		redisCacheManager.setExpires(map);
		return redisCacheManager;
	}
	
}

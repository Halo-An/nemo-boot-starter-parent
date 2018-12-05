package com.jimistore.boot.nemo.id.generator.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.jimistore.boot.nemo.id.generator.helper.IDGeneratorAspect;
import com.jimistore.boot.nemo.id.generator.helper.IDGeneratorHelper;
import com.jimistore.boot.nemo.id.generator.helper.NemoIdentifyGenerator;

@Configuration
public class NemoIdGeneratorAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(IDGeneratorHelper.class)
	public IDGeneratorHelper IDGeneratorHelper(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate) {
		IDGeneratorHelper helper = new IDGeneratorHelper().setRedisTemplate(redisTemplate);
		NemoIdentifyGenerator.setIDGeneratorHelper(helper);
		return helper;
	}

	@Bean
	@ConditionalOnMissingBean(IDGeneratorAspect.class)
	public IDGeneratorAspect IDGeneratorAspect(IDGeneratorHelper iDGeneratorHelper) {
		return new IDGeneratorAspect().setiDGeneratorHelper(iDGeneratorHelper);
	}
}

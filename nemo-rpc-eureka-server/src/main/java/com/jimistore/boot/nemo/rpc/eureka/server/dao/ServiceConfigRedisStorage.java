package com.jimistore.boot.nemo.rpc.eureka.server.dao;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.rpc.eureka.server.entity.ServiceConfig;

/**
 * 服务配置数据加载缓存实现(暂时废弃)
 * 
 * @author chenqi
 * @date 2020年11月10日
 *
 */
@Deprecated
public class ServiceConfigRedisStorage implements IServiceConfigStorage, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceConfigRedisStorage.class);

	@Value("${service.redis.key:nemo-monitor-service-list}")
	String redisServiceKey;

	@SuppressWarnings("rawtypes")
	@Autowired
	RedisTemplate redisTemplate;

	ObjectMapper objectMapper;

	@Override
	public ServiceConfig get(String id) {

		@SuppressWarnings("unchecked")
		HashOperations<String, String, String> ho = redisTemplate.opsForHash();
		String str = ho.get(redisServiceKey, id);
		// 如果找不到服务就读取默认的服务配置
		if (StringUtils.isBlank(str)) {
			str = ho.get(redisServiceKey, SERVICE_DEFAULT_KEY);
		}
		if (StringUtils.isNotBlank(str)) {
			try {
				ServiceConfig service = objectMapper.readValue(str, ServiceConfig.class);
				return service;
			} catch (Exception e) {
				LOG.warn(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

}

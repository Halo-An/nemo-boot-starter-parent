package com.jimistore.boot.nemo.rpc.eureka.helper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.googlecode.jsonrpc4j.JsonRpcService;
import com.jimistore.boot.nemo.core.util.AnnotationUtil;

/**
 * 动态RPCService输出类
 * 
 * @author chenqi
 * @Date 2018年10月7日
 *
 */
public class NemoDynamicRpcServiceExporter implements IDynamicRpcServiceExporter, BeanPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(NemoDynamicRpcServiceExporter.class);

	NemoAutoJsonRpcClientProxyCreatorHelper helper;

	Map<String, Object> beanCacheMap = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getRpcService(Class<T> inf, String serviceName, String version) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("request getRpcService, the serviceName is %s, the version is %s", serviceName,
					version));
		}
		String key = this.parseKey(inf, serviceName, version);
		if (!beanCacheMap.containsKey(key)) {
			T service = null;
			try {
				service = helper.getRegisteredBean(serviceName, inf, version);
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
			if (service == null) {
				JsonRpcService jsonRpcService = AnnotationUtil.getAnnotation(inf, JsonRpcService.class);
				helper.registerJsonProxyBean(inf.getName(), serviceName, version, jsonRpcService.value(),
						jsonRpcService.useNamedParams());
				service = helper.getRegisteredBean(serviceName, inf, version);
			}
			beanCacheMap.put(key, service);
		}
		return (T) beanCacheMap.get(key);
	}

	private String parseKey(Class<?> inf, String serviceName, String version) {
		return String.format("%s-%s-%s", inf.getName(), serviceName, version);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public NemoDynamicRpcServiceExporter setHelper(NemoAutoJsonRpcClientProxyCreatorHelper helper) {
		this.helper = helper;
		return this;
	}

}

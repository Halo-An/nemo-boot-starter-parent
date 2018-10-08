package com.jimistore.boot.nemo.rpc.eureka.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.googlecode.jsonrpc4j.JsonRpcService;
import com.jimistore.boot.nemo.core.util.AnnotationUtil;

/**
 * 动态RPCService输出类
 * @author chenqi
 * @Date 2018年10月7日
 *
 */
public class NemoDynamicRpcServiceExporter implements IDynamicRpcServiceExporter, BeanPostProcessor {
	
	private static final Logger log = Logger.getLogger(NemoDynamicRpcServiceExporter.class);
	
	NemoAutoJsonRpcClientProxyCreatorHelper helper;
	
	Map<String, Object> beanCacheMap = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getRpcService(Class<T> inf, String serviceName, String version) {
		if(!beanCacheMap.containsKey(serviceName)){
			JsonRpcService jsonRpcService = AnnotationUtil.getAnnotation(inf, JsonRpcService.class);
			helper.registerJsonProxyBean(inf.getName(), serviceName, version, jsonRpcService.value(), jsonRpcService.useNamedParams());
		}
		return (T)beanCacheMap.get(serviceName);
	}
	
	public void put(String serviceName, Object bean){
		beanCacheMap.put(serviceName, bean);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(beanName.indexOf("clientProxy")>=0){
			return bean;
		}
		log.info(String.format("=====================>%s", beanName));
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(beanName.indexOf("clientProxy")>=0||bean instanceof NemoJsonProxyFactoryBean){
//			NemoJsonProxyFactoryBean fac = (NemoJsonProxyFactoryBean) bean;
//			this.put(fac.getModule().getServiceName(), bean);
			return bean;
		}
		return bean;
	}

	public NemoDynamicRpcServiceExporter setHelper(NemoAutoJsonRpcClientProxyCreatorHelper helper) {
		this.helper = helper;
		return this;
	}

}

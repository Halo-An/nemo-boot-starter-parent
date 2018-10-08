package com.jimistore.boot.nemo.rpc.eureka.helper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.rpc.eureka.config.NemoRpcProperties;

/**
 * 客户端代理加载辅助类(修复RPC配置加载异常)
 * @author chenqi
 * @Date 2018年10月7日
 *
 */
public class NemoAutoJsonRpcClientProxyCreatorHelper implements InitializingBean, BeanPostProcessor, ApplicationContextAware {

    private ObjectMapper objectMapper;
    
    private NemoRpcProperties properties;
    
    private RestTemplate restTemplate;
	
    private ApplicationContext applicationContext;
    
    private NemoAutoJsonRpcClientProxyCreator nemoAutoJsonRpcClientProxyCreator;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		nemoAutoJsonRpcClientProxyCreator = new NemoAutoJsonRpcClientProxyCreator();
		nemoAutoJsonRpcClientProxyCreator.setObjectMapper(objectMapper);
		nemoAutoJsonRpcClientProxyCreator.setProperties(properties);
		nemoAutoJsonRpcClientProxyCreator.setRestTemplate(restTemplate);
		nemoAutoJsonRpcClientProxyCreator.setApplicationContext(applicationContext);
		
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		nemoAutoJsonRpcClientProxyCreator.postProcessBeanFactory(defaultListableBeanFactory);
	}

	public NemoAutoJsonRpcClientProxyCreatorHelper setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public NemoAutoJsonRpcClientProxyCreatorHelper setProperties(NemoRpcProperties properties) {
		this.properties = properties;
		return this;
	}

	public NemoAutoJsonRpcClientProxyCreatorHelper setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
    public void registerJsonProxyBean(String className, String module, String version, String path, boolean useNamedParams) {
    	nemoAutoJsonRpcClientProxyCreator.registerJsonProxyBean(className, module, version, path, useNamedParams);
    }

}
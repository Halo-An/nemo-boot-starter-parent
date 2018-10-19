package com.jimistore.boot.nemo.rpc.eureka.helper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 服务端代理加载辅助类(修复RPC配置加载异常)
 * @author chenqi
 * @Date 2018年10月7日
 *
 */
public class NemoAutoJsonRpcServiceExporterHelper implements InitializingBean, BeanPostProcessor, ApplicationContextAware {
	
	private ObjectMapper objectMapper;
	
    private ApplicationContext applicationContext;
    
	private NemoAutoJsonRpcServiceExporter nemoAutoJsonRpcServiceExporter;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		nemoAutoJsonRpcServiceExporter = new NemoAutoJsonRpcServiceExporter();
		nemoAutoJsonRpcServiceExporter.setObjectMapper(objectMapper);
		
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		nemoAutoJsonRpcServiceExporter.postProcessBeanFactory(defaultListableBeanFactory);
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	public NemoAutoJsonRpcServiceExporterHelper setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}



	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}



	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}

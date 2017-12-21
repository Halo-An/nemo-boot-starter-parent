package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQService;

/**
 * 处理收消息的客户端
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class MQReceiverClient implements BeanFactoryPostProcessor {
	
	IMQListener mQListener;
	
	Map<String,Object> mqMap = new HashMap<String,Object>();
	
	ObjectMapper objectMapper;

	public MQReceiverClient setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public MQReceiverClient setmQListener(IMQListener mQListener) {
		this.mQListener = mQListener;
		return this;
	}


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			JsonMQService jsonMQService = beanFactory.findAnnotationOnBean(beanName, JsonMQService.class);
			if(jsonMQService==null){
				continue;
			}
			Object target = beanFactory.getBean(beanName);
			Class<?> clazz = null;
			if(target instanceof FactoryBean){
				FactoryBean<?> f = (FactoryBean<?>)target;
				clazz = f.getObjectType();
			}else{
				try {
					clazz = this.getClass(beanFactory, beanName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			List<Method> methodList = this.listMethodByAnnotaion(clazz);
			for(Method method:methodList){
				String mQName = MQNameHelper.getMQNameByGroupAndMethod(clazz.getName(), jsonMQService.value(), method);
				mQListener.listener(new MQReceiverProxy()
						.setQueueType(jsonMQService.type())
						.setTarget(target)
						.setMsgClass(method.getParameterTypes())
						.setObjectMapper(objectMapper)
						.setmQDataSource(jsonMQService.dataSource())
						.setmQName(mQName));
			}
		}
	}
	
	
	
	/**
	 * Find a {@link BeanDefinition} in the {@link BeanFactory} or it's parents.
	 */
	private BeanDefinition findBeanDefintion(
		ConfigurableListableBeanFactory beanFactory, String serviceBeanName) {
		if (beanFactory.containsLocalBean(serviceBeanName)) {
			return beanFactory.getBeanDefinition(serviceBeanName);
		}
		BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
		if (parentBeanFactory != null
			&& ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
			return findBeanDefintion(
				(ConfigurableListableBeanFactory) parentBeanFactory,
				serviceBeanName);
		}
		throw new RuntimeException(String.format(
				"Bean with name '%s' can no longer be found.", serviceBeanName));
	}
	
	public Class<?> getClass(ConfigurableListableBeanFactory beanFactory, String serviceBeanName) throws ClassNotFoundException{
		BeanDefinition beanDefinition = this.findBeanDefintion(beanFactory, serviceBeanName);
 		String className = beanDefinition.getBeanClassName();
		return Class.forName(className);
	}
	
	private List<Method> listMethodByAnnotaion(Class<?> clazz){
		List<Method> methodList = new ArrayList<Method>();
		for(Class<?> iclass:clazz.getInterfaces()){
			if(iclass.isAnnotationPresent(JsonMQService.class)){
				for(Method method:iclass.getDeclaredMethods()){
					methodList.add(method);
				}
			}
		}
		return methodList;
	}
	

}

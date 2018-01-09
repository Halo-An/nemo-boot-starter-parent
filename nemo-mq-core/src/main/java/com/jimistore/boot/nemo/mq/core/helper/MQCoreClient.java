package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.annotation.EnableJsonMQ;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQService;

public class MQCoreClient implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {
	
	private static final Logger log = Logger.getLogger(MQCoreClient.class);
	
	IMQListener mQListener;
	
	IMQSender mQSender;
	
	ObjectMapper objectMapper;
	
	Map<String,Object> mqMap = new HashMap<String,Object>();
	
	Map<String, Class<?>> clazzMap = new HashMap<String, Class<?>>();
	
	Map<String, MQSenderProxy> senderMap = new HashMap<String, MQSenderProxy>();
	
	Map<String, JsonMQService> annoMap = new HashMap<String, JsonMQService>();
	
	ConfigurableListableBeanFactory beanFactory;
	
	ApplicationContext applicationContext;
	
	AsynExecuter asynExecuter;
	
	public MQCoreClient setAsynExecuter(AsynExecuter asynExecuter) {
		this.asynExecuter = asynExecuter;
		return this;
	}

	public MQCoreClient setmQListener(IMQListener mQListener) {
		this.mQListener = mQListener;
		return this;
	}

	public MQCoreClient setmQSender(IMQSender mQSender) {
		this.mQSender = mQSender;
		return this;
	}

	public MQCoreClient setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		
		SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);
        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        String[] scanPackages = null;
        
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
			EnableJsonMQ enableJsonMQ = beanFactory.findAnnotationOnBean(beanName, EnableJsonMQ.class);
        	if(enableJsonMQ!=null){
            	scanPackages = enableJsonMQ.value();
        	}
			JsonMQService jsonMQService = beanFactory.findAnnotationOnBean(beanName, JsonMQService.class);
			if(jsonMQService!=null){
				try {
					annoMap.put(beanName, jsonMQService);
					Class<?> clazz = this.getClass(beanFactory, beanName);
					clazzMap.put(beanName, clazz);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        }
        
        for(String scanPackage:scanPackages){
            String resolvedPath = resolvePackageToScan(scanPackage);
            log.debug(String.format("Scanning '%s' for JSON-MQ service interfaces.", resolvedPath));
            try {
                for (Resource resource : applicationContext.getResources(resolvedPath)) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        ClassMetadata classMetadata = metadataReader.getClassMetadata();
                        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                        String jsonRpcPathAnnotation = JsonMQService.class.getName();
                        if (annotationMetadata.isAnnotated(jsonRpcPathAnnotation)) {
                            String className = classMetadata.getClassName();
                            String mQGroup = (String) annotationMetadata.getAnnotationAttributes(jsonRpcPathAnnotation).get("value");
                            String dataSource = (String) annotationMetadata.getAnnotationAttributes(jsonRpcPathAnnotation).get("dataSource");
                            if(StringUtils.isEmpty(mQGroup)){
                            	mQGroup = className;
                            }
                            

                            String beanName = this.getExistKey(className);
                        	if(beanName!=null){
                        		MQSenderProxy mQSenderProxy = new MQSenderProxy()
                        				.setAsynExecuter(asynExecuter)
                        				.setObjectMapper(objectMapper)
                        				.setmQSender(mQSender)
                        				.setDataSource(dataSource)
                        				.setmQGroup(mQGroup);
                        		mQSenderProxy.setServiceInterface(Class.forName(className));
                        		
                        		senderMap.put(beanName, mQSenderProxy);
                        	}else{
                                log.debug(String.format("Found JSON-MQ service to proxy [%s] on mqGroup '%s'.", className, mQGroup));
                                //本地重新注册一个调用代理
                                registerJsonProxyBean(dlbf, className, dataSource, mQGroup);
                        	}

                        }
                    }
                }
                
            } catch (Exception e) {
                throw new RuntimeException(String.format("Cannot scan package '%s' for classes.", resolvedPath), e);
            }
    	}
	}
	
	private String getExistKey(String className){
		for(Entry<String, Class<?>> entry:clazzMap.entrySet()){
			try {
				if(Class.forName(className).isAssignableFrom(entry.getValue())){
					return entry.getKey();
				}
			} catch (ClassNotFoundException e) {
			}
		}
		return null;
	}
	
	private void registerJsonProxyBean(DefaultListableBeanFactory dlbf, String className, String dataSource, String mQGroup) {
		try {
			Class<?> serviceInterface = Class.forName(className);
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
	                .rootBeanDefinition(MQSenderProxy.class)
	                .addPropertyValue("asynExecuter", asynExecuter)
	                .addPropertyValue("dataSource", dataSource)
	                .addPropertyValue("mQGroup", mQGroup)
	                .addPropertyValue("serviceInterface", serviceInterface)
	                .addPropertyValue("objectMapper", objectMapper)
	                .addPropertyValue("mQSender", mQSender);
			dlbf.registerBeanDefinition(className+ "-clientProxy", beanDefinitionBuilder.getBeanDefinition());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		this.initReceiver(bean, beanName);
		if(senderMap.containsKey(beanName)){
			return senderMap.get(beanName);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
	
	private void initReceiver(Object target, String beanName){
		JsonMQService jsonMQService = annoMap.get(beanName);
		if(jsonMQService==null){
			return;
		}
		if(target==null){
			target = beanFactory.getBean(beanName);
		}
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
	
	private Class<?> getClass(ConfigurableListableBeanFactory beanFactory, String serviceBeanName) throws ClassNotFoundException{
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
	
	/**
     * Converts the scanPackage to something that the resource loader can handle.
     */
    private String resolvePackageToScan(String scanPackage) {
        return ResourceUtils.CLASSPATH_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanPackage) + "/**/*.class";
    }
}

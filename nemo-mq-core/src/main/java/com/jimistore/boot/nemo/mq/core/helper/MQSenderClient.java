package com.jimistore.boot.nemo.mq.core.helper;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
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
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.annotation.EnableJsonMQ;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQService;

/**
 * 处理发消息的客户端
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class MQSenderClient implements BeanFactoryPostProcessor, ApplicationContextAware {
	
	private static final Logger log = Logger.getLogger(MQSenderClient.class);
	
	ApplicationContext applicationContext;
	
	IMQSender mQSender;
	
	ObjectMapper objectMapper;

	public MQSenderClient setmQSender(IMQSender mQSender) {
		this.mQSender = mQSender;
		return this;
	}

	public MQSenderClient setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);
        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        String[] scanPackages = null;
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
			JsonMQService jsonMQService = beanFactory.findAnnotationOnBean(beanName, JsonMQService.class);
			EnableJsonMQ enableJsonMQ = beanFactory.findAnnotationOnBean(beanName, EnableJsonMQ.class);
        	if(enableJsonMQ!=null){
            	scanPackages = enableJsonMQ.value();
        	}
			if(jsonMQService==null){
				continue;
			}
	        //从本地移除这些bean
    		dlbf.removeBeanDefinition(beanName);
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
                            log.debug(String.format("Found JSON-MQ service to proxy [%s] on mqGroup '%s'.", className, mQGroup));
                            //本地重新注册一个调用代理
                            registerJsonProxyBean(dlbf, className, dataSource, mQGroup);

                        }
                    }
                }
                
            } catch (IOException e) {
                throw new RuntimeException(String.format("Cannot scan package '%s' for classes.", resolvedPath), e);
            }
    	}
	}

	private void registerJsonProxyBean(DefaultListableBeanFactory dlbf, String className, String dataSource, String mQGroup) {
		try {
			Class<?> serviceInterface = Class.forName(className);
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
	                .rootBeanDefinition(MQSenderProxy.class)
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
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	/**
     * Converts the scanPackage to something that the resource loader can handle.
     */
    private String resolvePackageToScan(String scanPackage) {
        return ResourceUtils.CLASSPATH_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanPackage) + "/**/*.class";
    }

}

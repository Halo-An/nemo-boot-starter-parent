package com.jimistore.boot.nemo.rpc.eureka.helper;

import static java.lang.String.format;
import static org.springframework.util.ClassUtils.convertClassNameToResourcePath;
import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcService;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcClientProxyCreator;
import com.jimistore.boot.nemo.rpc.eureka.config.NemoRpcItem;

/**
 * 修改默认异常处理
 * @author qi
 *
 */
public class NemoAutoJsonRpcClientProxyCreator extends AutoJsonRpcClientProxyCreator {
	private static final Logger LOG = Logger.getLogger(AutoJsonRpcClientProxyCreator.class.getName());

    private ApplicationContext applicationContext;

    private ObjectMapper objectMapper;
    
    private Map<String, NemoRpcItem> rpcMap;
    
    private INemoRpcClusterExporter nemoRpcClusterExporter;
    
    private RestTemplate restTemplate;

	public NemoAutoJsonRpcClientProxyCreator setRpcMap(Map<String, NemoRpcItem> rpcMap) {
		this.rpcMap = rpcMap;
		return this;
	}

	@Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);
        DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
        
        try{
        	this.nemoRpcClusterExporter = beanFactory.getBean(INemoRpcClusterExporter.class);
        }catch(Exception e){
        	
        }
        
        if(restTemplate==null){
            try{
            	this.restTemplate = beanFactory.getBean(RestTemplate.class);
            }catch(Exception e){
            	
            }
        }
        
        if(rpcMap!=null&&rpcMap.size()>0){
			for(Entry<String, NemoRpcItem> entry:rpcMap.entrySet()){
				NemoRpcItem nemoRpcConfig = entry.getValue();
				String scanPackage = nemoRpcConfig.getScan();
				String module = entry.getKey();
				String version = nemoRpcConfig.getVersion();
        
		        String resolvedPath = resolvePackageToScan(scanPackage);
		        LOG.fine(format("Scanning '%s' for JSON-RPC service interfaces.", resolvedPath));
		        try {
		            for (Resource resource : applicationContext.getResources(resolvedPath)) {
		                if (resource.isReadable()) {
		                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
		                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
		                    AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		                    String jsonRpcPathAnnotation = JsonRpcService.class.getName();
		                    if (annotationMetadata.isAnnotated(jsonRpcPathAnnotation)) {
		                        String className = classMetadata.getClassName();
		                        String path = (String) annotationMetadata.getAnnotationAttributes(jsonRpcPathAnnotation).get("value");
		                        boolean useNamedParams = (Boolean) annotationMetadata.getAnnotationAttributes(jsonRpcPathAnnotation).get("useNamedParams");
		                        LOG.fine(format("Found JSON-RPC service to proxy [%s] on path '%s'.", className, path));
		                        registerJsonProxyBean(dlbf, className, module, version, path, useNamedParams);
		                    }
		                }
		            }
		        } catch (IOException e) {
		            throw new RuntimeException(format("Cannot scan package '%s' for classes.", resolvedPath), e);
		        }
		        
			}
        }
        
        
        
    }

    /**
     * Converts the scanPackage to something that the resource loader can handle.
     */
    private String resolvePackageToScan(String scanPackage) {
        return CLASSPATH_URL_PREFIX + convertClassNameToResourcePath(scanPackage) + "/**/*.class";
    }

    /**
     * Registers a new proxy bean with the bean factory.
     */
    private void registerJsonProxyBean(DefaultListableBeanFactory dlbf, String className, String module, String version, String path, boolean useNamedParams) {
    	BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(NemoJsonProxyFactoryBean.class)
                .addPropertyValue("path", path)
                .addPropertyValue("module", module)
                .addPropertyValue("version", version)
                .addPropertyValue("serviceInterface", className)
                .addPropertyValue("useNamedParams", useNamedParams);
        if (objectMapper != null) {
            beanDefinitionBuilder.addPropertyValue("objectMapper", objectMapper);
        }
        if (nemoRpcClusterExporter != null) {
        	beanDefinitionBuilder.addPropertyValue("nemoRpcClusterExporter", nemoRpcClusterExporter);
        	beanDefinitionBuilder.addPropertyValue("serviceUrl", "http://localhost");
        }
        if(restTemplate!=null){
        	beanDefinitionBuilder.addPropertyValue("restTemplate", restTemplate);
        }
        dlbf.registerBeanDefinition(className+ "-" + module + "-clientProxy", beanDefinitionBuilder.getBeanDefinition());
    }

    /**
     * Appends the base path to the path found in the interface.
     */
    @SuppressWarnings("unused")
	private String appendBasePath(String baseUrl,String path) {
        try {
            return new URL(new URL(baseUrl), path).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(format("Cannot combine URLs '%s' and '%s' to valid URL.", baseUrl, path), e);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
    
}

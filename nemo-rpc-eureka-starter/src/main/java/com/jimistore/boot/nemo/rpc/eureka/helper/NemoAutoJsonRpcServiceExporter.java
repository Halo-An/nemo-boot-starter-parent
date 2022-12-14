package com.jimistore.boot.nemo.rpc.eureka.helper;

import static java.lang.String.format;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.getAllInterfacesForClass;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.HttpRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.ErrorResolver;
import com.googlecode.jsonrpc4j.InvocationListener;
import com.googlecode.jsonrpc4j.JsonRpcService;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceExporter;

/**
 * {@link HttpRequestHandler} that exports services using Json
 * according to the JSON-RPC proposal specified at:
 * <a href="http://groups.google.com/group/json-rpc">
 * http://groups.google.com/group/json-rpc</a>.
 *
 */
public class NemoAutoJsonRpcServiceExporter
	extends AutoJsonRpcServiceExporter{
	private static final Logger LOG = Logger.getLogger(AutoJsonRpcServiceExporter.class.getName());

	private static final String PATH_PREFIX = "/";

	private Map<String, String> serviceBeanNames = new HashMap<String, String>();

	private ObjectMapper objectMapper;
	private ErrorResolver errorResolver = null;
	private Boolean registerTraceInterceptor;
	private boolean backwardsComaptible = true;
	private boolean rethrowExceptions = false;
	private boolean allowExtraParams = false;
	private boolean allowLessParams = false;
	private Level exceptionLogLevel = Level.WARNING;
    private InvocationListener invocationListener = null;

	public void postProcessBeanFactory(
		ConfigurableListableBeanFactory beanFactory)
		throws BeansException {
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;
		findServiceBeanDefinitions(dlbf);
		for (Entry<String, String> entry : serviceBeanNames.entrySet()) {
			String servicePath = entry.getKey();
			String serviceBeanName = entry.getValue();
			registerServiceProxy(dlbf, makeUrlPath(servicePath), serviceBeanName);
		}
	}

	/**
	 * Finds the beans to expose and puts them in the {@link #serviceBeanNames}
	 * map.
	 * <p>
	 * Searches parent factories as well.
	 */
	private void findServiceBeanDefinitions(
		ConfigurableListableBeanFactory beanFactory) {
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			JsonRpcService jsonRpcPath = beanFactory.findAnnotationOnBean(beanName, JsonRpcService.class);
			if (jsonRpcPath != null) {
				String pathValue = jsonRpcPath.value();
				LOG.fine(
					format("Found JSON-RPC path '%s' for bean [%s].",
					pathValue, beanName));
				if (serviceBeanNames.containsKey(pathValue)) {
					String otherBeanName = serviceBeanNames.get(pathValue);
					LOG.warning(format(
						"Duplicate JSON-RPC path specification: found %s on both [%s] and [%s].",
						pathValue, beanName, otherBeanName));
				}
				serviceBeanNames.put(pathValue, beanName);
			}
		}
		BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
		if (parentBeanFactory != null 
			&& ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
			findServiceBeanDefinitions((ConfigurableListableBeanFactory) parentBeanFactory);
		}
	}

	/**
	 * To make the
	 * {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping}
	 * export a bean automatically, the name should start with a '/'.
	 */
	private String makeUrlPath(String servicePath) {
		return PATH_PREFIX.concat(servicePath);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Class getInterfaceOfJsonRpcService(Class iface){
		if(iface!=null){
			if(iface.isAnnotationPresent(JsonRpcService.class)){
				return iface;
			}
			Class[] ifaces = iface.getInterfaces();
			if(ifaces!=null){
				for(Class ifa:ifaces){
					ifa=this.getInterfaceOfJsonRpcService(ifa);
					if(ifa!=null){
						return ifa;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Registers the new beans with the bean factory.
	 */
	private void registerServiceProxy(
		DefaultListableBeanFactory dlbf, String servicePath, String serviceBeanName) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder
			.rootBeanDefinition(NemoJsonServiceExporter.class)
			.addPropertyReference("service", serviceBeanName);
		BeanDefinition serviceBeanDefinition = findBeanDefintion(dlbf, serviceBeanName);
		for (Class<?> iface :
			getBeanInterfaces(serviceBeanDefinition, dlbf.getBeanClassLoader())) {
			iface = this.getInterfaceOfJsonRpcService(iface);
			if (iface!=null) {
				String serviceInterface = iface.getName();
				LOG.fine(format(
					"Registering interface '%s' for JSON-RPC bean [%s].",
					serviceInterface, serviceBeanName));
				builder.addPropertyValue("serviceInterface", serviceInterface);
				break;
			}
		}
		if (objectMapper != null) {
			builder.addPropertyValue("objectMapper", objectMapper);
		}

		if (errorResolver != null) {
			builder.addPropertyValue("errorResolver", errorResolver);
		}

        if (invocationListener != null) {
            builder.addPropertyValue("invocationListener", invocationListener);
        }

		if(registerTraceInterceptor != null) {
			builder.addPropertyValue("registerTraceInterceptor", registerTraceInterceptor);
		}

		builder.addPropertyValue("backwardsComaptible", Boolean.valueOf(backwardsComaptible));
		builder.addPropertyValue("rethrowExceptions", Boolean.valueOf(rethrowExceptions));
		builder.addPropertyValue("allowExtraParams", Boolean.valueOf(allowExtraParams));
		builder.addPropertyValue("allowLessParams", Boolean.valueOf(allowLessParams));
		builder.addPropertyValue("exceptionLogLevel", exceptionLogLevel);
		dlbf.registerBeanDefinition(servicePath, builder.getBeanDefinition());
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
		throw new RuntimeException(format(
				"Bean with name '%s' can no longer be found.", serviceBeanName));
	}

	private Class<?>[] getBeanInterfaces(
		BeanDefinition serviceBeanDefinition, ClassLoader beanClassLoader) {
		String beanClassName = serviceBeanDefinition.getBeanClassName();
		try {
			Class<?> beanClass = forName(beanClassName, beanClassLoader);
			return getAllInterfacesForClass(beanClass, beanClassLoader);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(format("Cannot find bean class '%s'.",
					beanClassName), e);
		} catch (LinkageError e) {
			throw new RuntimeException(format("Cannot find bean class '%s'.",
					beanClassName), e);
		}
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * @param errorResolver the errorResolver to set
	 */
	public void setErrorResolver(ErrorResolver errorResolver) {
		this.errorResolver = errorResolver;
	}

	/**
	 * @param backwardsComaptible the backwardsComaptible to set
	 */
	public void setBackwardsComaptible(boolean backwardsComaptible) {
		this.backwardsComaptible = backwardsComaptible;
	}

	/**
	 * @param rethrowExceptions the rethrowExceptions to set
	 */
	public void setRethrowExceptions(boolean rethrowExceptions) {
		this.rethrowExceptions = rethrowExceptions;
	}

	/**
	 * @param allowExtraParams the allowExtraParams to set
	 */
	public void setAllowExtraParams(boolean allowExtraParams) {
		this.allowExtraParams = allowExtraParams;
	}

	/**
	 * @param allowLessParams the allowLessParams to set
	 */
	public void setAllowLessParams(boolean allowLessParams) {
		this.allowLessParams = allowLessParams;
	}

	/**
	 * @param exceptionLogLevel the exceptionLogLevel to set
	 */
	public void setExceptionLogLevel(Level exceptionLogLevel) {
		this.exceptionLogLevel = exceptionLogLevel;
	}

	/**
	 * See {@link org.springframework.remoting.support.RemoteExporter#setRegisterTraceInterceptor(boolean)}
	 * @param registerTraceInterceptor the registerTraceInterceptor value to set
	 */
	public void setRegisterTraceInterceptor(boolean registerTraceInterceptor) {
		this.registerTraceInterceptor = registerTraceInterceptor;
	}

    /**
     * @param invocationListener the invocationListener to set
     */
    public void setInvocationListener(InvocationListener invocationListener) {
        this.invocationListener = invocationListener;
    }

}
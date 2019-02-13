package com.jimistore.boot.nemo.rpc.eureka.helper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.ReflectionUtil;
import com.googlecode.jsonrpc4j.spring.JsonProxyFactoryBean;
import com.jimistore.boot.nemo.fuse.core.FuseTemplate;
import com.jimistore.boot.nemo.fuse.core.ITask;
import com.jimistore.boot.nemo.rpc.eureka.config.NemoRpcProperties;

public class NemoJsonProxyFactoryBean extends JsonProxyFactoryBean {
	
	private INemoRpcClusterExporter nemoRpcClusterExporter  = null;
	private RestTemplate        restTemplate        = null;
	private String              path				= null;
	private IModuleExporter     module				= null;
	private String              version				= null;

	private boolean				useNamedParams		= false;
	private Object				proxyObject			= null;
	private ObjectMapper		objectMapper		= null;
	private IJsonRpcClient		jsonRpcClient	= null;
	private Map<String, String>	extraHttpHeaders	= new HashMap<String, String>();
    
    
	private ApplicationContext	applicationContext;
	
	private NemoRpcProperties 	properties;
	private FuseTemplate 		fuseTemplate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() {
//		super.afterPropertiesSet();

		// create proxy
		proxyObject = ProxyFactory.getProxy(getServiceInterface(), this);

		// find the ObjectMapper
		if (objectMapper == null
			&& applicationContext != null
			&& applicationContext.containsBean("objectMapper")) {
			objectMapper = (ObjectMapper) applicationContext.getBean("objectMapper");
		}
		if (objectMapper == null && applicationContext != null) {
			try {
				objectMapper = (ObjectMapper)BeanFactoryUtils
					.beanOfTypeIncludingAncestors(applicationContext, ObjectMapper.class);
			} catch (Exception e) { /* no-op */ }
		}
		if (objectMapper==null) {
			objectMapper = new ObjectMapper();
		}

		URL url = null;
		try{
			url = new URL(getServiceUrl());
		} catch (MalformedURLException mue) {
//			throw new RuntimeException(mue);
		}
		
		// create JsonRpcClient
		if(restTemplate==null){
			jsonRpcClient = new NemoJsonRpcHttpClient(nemoRpcClusterExporter, objectMapper, url, module, version, path, extraHttpHeaders);
		}else{
			jsonRpcClient = new NemoJsonRpcRestTemplateClient(objectMapper)
					.setServiceUrl(url)
					.setPath(path)
					.setModule(module)
					.setVersion(version)
					.setRestTemplate(restTemplate)
					.setNemoRpcClusterExporter(nemoRpcClusterExporter);
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object invoke(MethodInvocation invocation)
		throws Throwable {

		// handle toString()
		Method method = invocation.getMethod();
		if (method.getDeclaringClass() == Object.class && method.getName().equals("toString")) {
			return proxyObject.getClass().getName() + "@" + System.identityHashCode(proxyObject);
		}
		// get return type
		Type retType = (invocation.getMethod().getGenericReturnType() != null)
			? invocation.getMethod().getGenericReturnType()
			: invocation.getMethod().getReturnType();

		// get arguments
		Object arguments = ReflectionUtil.parseArguments(
			invocation.getMethod(), invocation.getArguments(), useNamedParams);


		// invoke it
		if(fuseTemplate!=null){
			StringBuilder key = new StringBuilder("rpc-")
					.append(module.getServiceName())
					.append("-")
					.append(version)
					.append(":")
					.append(method.getName())
					.append("(");
			for(Class<?> paramType:method.getParameterTypes()) {
				key.append(paramType.getName()).append(",");
			}
			if(method.getParameterTypes().length>0) {
				key.deleteCharAt(key.length()-1);
			}
			key.append(")");
			return fuseTemplate.execute(key.toString(), new ITask<Object>(){

				@Override
				public Object call() throws Exception {
					try {
						return jsonRpcClient.invoke(
							method.getName(),
							arguments,
							retType, extraHttpHeaders);
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public long getTimeout() {
					return properties.getFuseTimeOut();
				}

				@Override
				public Callable<Object> getCallback() {
					return null;
				}
				
			});
		}
		return jsonRpcClient.invoke(
			method.getName(),
			arguments,
			retType, extraHttpHeaders);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getObject() {
		return proxyObject;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<?> getObjectType() {
		return getServiceInterface();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		super.setApplicationContext(applicationContext);
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		super.setObjectMapper(objectMapper);
	}

	public void setModule(IModuleExporter module) {
		this.module = module;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setNemoRpcClusterExporter(INemoRpcClusterExporter nemoRpcClusterExporter) {
		this.nemoRpcClusterExporter = nemoRpcClusterExporter;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public IModuleExporter getModule() {
		return module;
	}

	public void setProperties(NemoRpcProperties properties) {
		this.properties = properties;
	}

	public void setFuseTemplate(FuseTemplate fuseTemplate) {
		this.fuseTemplate = fuseTemplate;
	}
	
}

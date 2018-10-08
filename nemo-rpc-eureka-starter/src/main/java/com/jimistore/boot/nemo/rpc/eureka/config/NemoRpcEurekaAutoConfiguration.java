package com.jimistore.boot.nemo.rpc.eureka.config;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoAutoJsonRpcClientProxyCreatorHelper;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoAutoJsonRpcServiceExporterHelper;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoDynamicRpcServiceExporter;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoRpcEurekaRibbonExporter;
import com.jimistore.boot.nemo.rpc.eureka.helper.TextXmlMappingJackson2HttpMessageConverter;

@Configuration
@EnableConfigurationProperties(NemoRpcProperties.class)
public class NemoRpcEurekaAutoConfiguration implements EnvironmentAware {
	
	
	Map<String, NemoRpcItem> map = new HashMap<String, NemoRpcItem>();

	@Override
	public void setEnvironment(Environment environment) {

		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
				environment, "rpc.");
		// 获取到所有数据源的名称.
		String dsPrefixs = propertyResolver.getProperty("names");
		if(dsPrefixs==null){
			return ;
		}
		String[] dsPrefixsArr = dsPrefixs.split(",");
		if(dsPrefixsArr==null){
			return ;
		}
		for (String dsPrefix : dsPrefixsArr) {
			Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
			if(dsMap==null){
				return ;
			}
			NemoRpcItem nemoRpcConfig=new NemoRpcItem();
			try{
			nemoRpcConfig.setScan(dsMap.get("scan").toString());
			}catch(Exception e){}
			try{
			nemoRpcConfig.setVersion(dsMap.get("version").toString());
			}catch(Exception e){}
			map.put(dsPrefix, nemoRpcConfig);
		}
	}
	
	
	@Bean
	public NemoRpcEurekaRibbonExporter NemoRpcClusterExporter(@Lazy LoadBalancerClient loadBalancerClient){
		return new NemoRpcEurekaRibbonExporter().setLoadBalancerClient(loadBalancerClient);
	}
	
	@Bean
	@ConditionalOnMissingBean(RestTemplate.class)
	public RestTemplate restTemplate(NemoRpcProperties properties){
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(properties.getReadTimeOut());
        requestFactory.setConnectTimeout(properties.getConnectTimeOut());

        //支持text/xml方式的json response
        RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.getMessageConverters().add(new TextXmlMappingJackson2HttpMessageConverter());
		return restTemplate;
	}
	
	@Bean
	@ConditionalOnMissingBean(NemoAutoJsonRpcServiceExporterHelper.class)
	public NemoAutoJsonRpcServiceExporterHelper gennerateExporter(NemoRpcProperties properties){
		ObjectMapper objectMapper = new ObjectMapper();
		if(properties.isIgnoreVersionCompatible()){
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		NemoAutoJsonRpcServiceExporterHelper nemoAutoJsonRpcServiceExporter = new NemoAutoJsonRpcServiceExporterHelper();
		nemoAutoJsonRpcServiceExporter.setObjectMapper(objectMapper);
		return nemoAutoJsonRpcServiceExporter;
	}
	
	@Bean
	@ConditionalOnMissingBean(NemoAutoJsonRpcClientProxyCreatorHelper.class)
	public NemoAutoJsonRpcClientProxyCreatorHelper gennerateCreator(NemoRpcProperties properties, RestTemplate restTemplate){
		ObjectMapper objectMapper = new ObjectMapper();
		if(properties.isIgnoreVersionCompatible()){
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		if(properties.getMap()!=null){
			properties.getMap().putAll(map);
		}else{
			properties.setMap(map);
		}
		NemoAutoJsonRpcClientProxyCreatorHelper helper = new NemoAutoJsonRpcClientProxyCreatorHelper()
				.setRestTemplate(restTemplate)
				.setProperties(properties)
				.setObjectMapper(objectMapper);
		return helper;
	}
	
	@Bean
	@ConditionalOnMissingBean(NemoDynamicRpcServiceExporter.class)
	public NemoDynamicRpcServiceExporter NemoDynamicRpcServiceExporter(NemoAutoJsonRpcClientProxyCreatorHelper helper){
		return new NemoDynamicRpcServiceExporter().setHelper(helper);
	}

}

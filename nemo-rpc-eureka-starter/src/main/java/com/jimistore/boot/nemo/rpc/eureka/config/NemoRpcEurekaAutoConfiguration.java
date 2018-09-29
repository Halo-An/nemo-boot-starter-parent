package com.jimistore.boot.nemo.rpc.eureka.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoAutoJsonRpcClientProxyCreator;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoAutoJsonRpcServiceExporter;
import com.jimistore.boot.nemo.rpc.eureka.helper.NemoRpcEurekaRibbonExporter;
import com.jimistore.boot.nemo.rpc.eureka.helper.TextXmlMappingJackson2HttpMessageConverter;

@Configuration
@ConditionalOnClass(LoadBalancerClient.class)
@ConditionalOnBean(LoadBalancerClient.class)
@AutoConfigureAfter(name = "org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration")
@EnableConfigurationProperties(NemoRpcProperties.class)
public class NemoRpcEurekaAutoConfiguration {
	
	
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
	@ConditionalOnMissingBean(NemoAutoJsonRpcServiceExporter.class)
	public NemoAutoJsonRpcServiceExporter gennerateExporter(NemoRpcProperties properties){
		ObjectMapper objectMapper = new ObjectMapper();
		if(properties.isIgnoreVersionCompatible()){
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		NemoAutoJsonRpcServiceExporter nemoAutoJsonRpcServiceExporter = new NemoAutoJsonRpcServiceExporter();
		nemoAutoJsonRpcServiceExporter.setObjectMapper(objectMapper);
		return nemoAutoJsonRpcServiceExporter;
	}
	
	@Bean
	@ConditionalOnMissingBean(NemoAutoJsonRpcClientProxyCreator.class)
	public NemoAutoJsonRpcClientProxyCreator gennerateCreator(NemoRpcProperties properties){
		ObjectMapper objectMapper = new ObjectMapper();
		if(properties.isIgnoreVersionCompatible()){
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		NemoAutoJsonRpcClientProxyCreator helper = new NemoAutoJsonRpcClientProxyCreator();
		helper.setRpcMap(properties.getMap());
		helper.setObjectMapper(objectMapper);
		return helper;
	}

}

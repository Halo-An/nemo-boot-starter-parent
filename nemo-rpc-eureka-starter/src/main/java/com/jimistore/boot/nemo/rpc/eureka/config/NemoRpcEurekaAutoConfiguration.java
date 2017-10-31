package com.jimistore.boot.nemo.rpc.eureka.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.jimistore.boot.nemo.rpc.eureka.helper.NemoRpcEurekaRibbonExporter;

@Configuration
@ConditionalOnClass(LoadBalancerClient.class)
@ConditionalOnBean(LoadBalancerClient.class)
@AutoConfigureAfter(name = "org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration")
public class NemoRpcEurekaAutoConfiguration {
	
	
	@Bean
	public NemoRpcEurekaRibbonExporter NemoRpcClusterExporter(@Lazy LoadBalancerClient loadBalancerClient){
		return new NemoRpcEurekaRibbonExporter().setLoadBalancerClient(loadBalancerClient);
	}

}

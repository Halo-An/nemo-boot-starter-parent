package com.jimistore.boot.nemo.gateway.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jimistore.boot.nemo.gateway.server.core.GatewayController;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class NemoGatewayAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(GatewayController.class)
	public GatewayController gatewayController() {
		return new GatewayController();
	}

}

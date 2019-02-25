package com.jimistore.boot.nemo.monitor.server.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

import com.jimistore.boot.nemo.monitor.server.helper.TextXmlMappingJackson2HttpMessageConverter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll();
		http.logout().logoutUrl("/logout");
		http.csrf().disable();

		http.authorizeRequests().antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**").permitAll();

		http.authorizeRequests().antMatchers("/api/**").permitAll().antMatchers("/**").authenticated();

		// Enable so that the clients can authenticate via HTTP basic for
		// registering
		http.httpBasic();
	}

	@Bean
	@ConditionalOnMissingBean(RestTemplate.class)
	public RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		requestFactory.setReadTimeout(10000);
		requestFactory.setConnectTimeout(3000);

		// 支持text/xml方式的json response
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		restTemplate.getMessageConverters().add(new TextXmlMappingJackson2HttpMessageConverter());
		return restTemplate;
	}
}

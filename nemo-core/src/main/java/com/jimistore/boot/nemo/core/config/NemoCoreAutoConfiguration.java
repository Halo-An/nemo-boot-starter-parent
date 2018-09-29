package com.jimistore.boot.nemo.core.config;

import java.util.Locale;

import javax.validation.MessageInterpolator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.jimistore.boot.nemo.core.helper.InitContextAspect;
import com.jimistore.boot.nemo.core.helper.NemoMethodValidationPostProcessor;
import com.jimistore.boot.nemo.core.helper.RequestLoggerAspect;
import com.jimistore.boot.nemo.core.helper.ResponseBodyWrapFactory;
import com.jimistore.boot.nemo.core.helper.ResponseExceptionHandle;

@Configuration
public class NemoCoreAutoConfiguration {

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**");
                registry.addMapping("/meta/**");
            }
        };
    }
	
	@Bean
	public InitContextAspect InitContextAspect(){
		return new InitContextAspect();
	}
	

	
	@Bean
	public ResponseBodyWrapFactory responseBodyWrapFactory(){
		return new ResponseBodyWrapFactory();
	}


	@Bean
	public ResponseExceptionHandle responseExceptionHandle(){
		return new ResponseExceptionHandle();
	}
	
	@Bean
	public RequestLoggerAspect requestLoggerAspect(){
		return new RequestLoggerAspect();
	}
	
	
	
	@Bean
	public MessageInterpolator messageInterpolator(){
		return new MessageInterpolator(){

			@Override
			public String interpolate(String messageTemplate, Context context) {
				return null;
			}

			@Override
			public String interpolate(String messageTemplate, Context context,
					Locale locale) {
				
				if(messageTemplate.indexOf("NotNull")>=0 || messageTemplate.indexOf("NotBlank")>=0){
					return " ${field} cannot be empty. ";
				}
				if(messageTemplate.indexOf("{")>=0){
					return " ${field} format error. ";
				}
				return messageTemplate;
			}
			
		};
	}

	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean(MessageInterpolator messageInterpolator) {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setMessageInterpolator(messageInterpolator);
		return localValidatorFactoryBean;
	}
	
	
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean localValidatorFactoryBean){
		NemoMethodValidationPostProcessor methodValidationPostProcessor =new NemoMethodValidationPostProcessor();
		methodValidationPostProcessor.setValidatorFactory(localValidatorFactoryBean);
		methodValidationPostProcessor.setOrder(1);
		return methodValidationPostProcessor;
	}
	
}

package com.jimistore.boot.nemo.mq.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.mq.core.config.NemoMQCoreConfiguration;

/**
 * 定义是否开启JsonMQService功能
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NemoMQCoreConfiguration.class)
public @interface EnableJsonMQ {
	
	String[] value() default {"com.jimistore"};
	
}

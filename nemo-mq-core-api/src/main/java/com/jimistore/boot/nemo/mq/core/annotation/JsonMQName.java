package com.jimistore.boot.nemo.mq.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明mq的名称
 * @author chenqi
 * @Date 2018年5月14日
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonMQName {
	
	/**
	 * The actual value expression: e.g. "#p0.getName()".
	 */
	String value();
	
}

package com.jimistore.boot.nemo.mq.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记某接口是否需要支持异步调用
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonMQService {
	
	/**
	 * 数据源.
	 */
	String value() default "default";
}

package com.jimistore.boot.nemo.mq.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jimistore.boot.nemo.mq.core.enums.QueueType;

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
	 * The actual value expression: e.g. "#p0.getName()".
	 */
	String value() default "";
	
	/**
	 * 
	 * @return
	 */
	String dataSource() default "default";
	
	QueueType type() default QueueType.Queue;
}

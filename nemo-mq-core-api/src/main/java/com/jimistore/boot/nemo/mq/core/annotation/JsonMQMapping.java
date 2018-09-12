package com.jimistore.boot.nemo.mq.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jimistore.boot.nemo.mq.core.enums.QueueType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonMQMapping {
	
	public static final String DEFAULT_TAG="*";
	
	QueueType type() default QueueType.Queue;
	
	String value() default "";
	
	String tag() default DEFAULT_TAG;
	
	String delay() default "0";
	
	String key() default "";
	
	String shardingKey() default "";

}

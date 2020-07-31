package com.jimistore.boot.nemo.sliding.window.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Topic {

	/**
	 * 标识
	 * 
	 * @return
	 */
	String value() default "";

	/**
	 * 时间达内
	 * 
	 * @return
	 */
	TimeUnit timeUnit() default TimeUnit.SECONDS;

	/**
	 * 条件
	 * 
	 * @return
	 */
	String condition() default "true";

	/**
	 * 计数
	 * 
	 * @return
	 */
	String num() default "1";

	/**
	 * 容量
	 * 
	 * @return
	 */
	int capacity() default 1440;

	/**
	 * 是否强制创建
	 * 
	 * @return
	 */
	boolean force() default false;

	/**
	 * 值类型
	 * 
	 * @return
	 */
	Class<?> valueType() default Integer.class;

}

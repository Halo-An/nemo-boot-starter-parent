package com.jimistore.boot.nemo.dao.hibernate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Select {

	/**
	 * sql表达式
	 * 
	 * @return
	 */
	String[] value() default {};

	/**
	 * 每页大小表达式
	 * 
	 * @return
	 */
	String pageSize() default "${#pageSize?:10}";

	/**
	 * 页码表达式
	 * 
	 * @return
	 */
	String pageNum() default "${#pageNum?:1}";
}

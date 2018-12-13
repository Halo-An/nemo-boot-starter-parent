package com.jimistore.boot.nemo.id.generator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IDGenerator {
	
	/**
	 * 唯一标识
	 * @return
	 */
	String value();
	
	/**
	 * 生成id用的字符序列
	 * @return
	 */
	String sequence() default "8907213546";
	
	/**
	 * 生成的字符长度
	 * @return
	 */
	int length() default 6;
	
	/**
	 * 生成id后输出的字段
	 * @return
	 */
	int field() default 0;
	
	/**
	 * 开始的序号
	 * @return
	 */
	long start() default 1000;
	
	/**
	 * 生成器的类
	 * @return
	 */
	Class<?> generatorClass() default com.jimistore.boot.nemo.id.generator.core.IDGenerator.class;

}

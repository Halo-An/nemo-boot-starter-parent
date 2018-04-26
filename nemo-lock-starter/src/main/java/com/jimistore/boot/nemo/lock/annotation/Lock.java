package com.jimistore.boot.nemo.lock.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
	
	/**
	 * The actual value expression: e.g. "#p0.getName()".
	 */
	String value();
	
	String prompt() default "lock failed, please try again later";
	
	String operator() default "";
	
	long timeout() default 10000l;
}

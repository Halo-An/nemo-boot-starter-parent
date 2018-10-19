package com.jimistore.boot.nemo.sliding.window.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Topic {
	
	String value() default "";
	
	TimeUnit timeUnit() default TimeUnit.SECONDS;
	
	String condition() default "true";
	
	String num() default "1";
	
	int capacity() default 1440;
	
	Class<?> valueType() default Integer.class;

}

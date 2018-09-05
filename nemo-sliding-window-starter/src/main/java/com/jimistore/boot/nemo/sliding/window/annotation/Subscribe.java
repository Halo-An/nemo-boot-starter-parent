package com.jimistore.boot.nemo.sliding.window.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {
	
	String value();
	
	TimeUnit timeUnit() default TimeUnit.SECONDS;
	
	int length() default 300;
	
	int interval() default 0;
	
	String condition() default "true";
	
	Class<?> valueType() default Integer.class;

}

package com.jimistore.boot.nemo.high.concurrency.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockStockCreate {
	
	/**
	 * The actual value expression: e.g. "#p0.getName()".
	 */
	String key();
	
	/**
	 * The actual value expression: e.g. "#p0.getStock()".
	 */
	String num();
	
	long timeout();
}

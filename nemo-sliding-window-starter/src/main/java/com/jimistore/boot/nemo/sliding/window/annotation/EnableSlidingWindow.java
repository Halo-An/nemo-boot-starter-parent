package com.jimistore.boot.nemo.sliding.window.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.sliding.window.config.NemoSlidingWindowConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NemoSlidingWindowConfiguration.class)
public @interface EnableSlidingWindow {
	
}

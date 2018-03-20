package com.jimistore.boot.nemo.high.concurrency.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.high.concurrency.config.NemoHighConcurrencyConfiguration;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({NemoHighConcurrencyConfiguration.class})
public @interface EnableHighConcurrency {

}

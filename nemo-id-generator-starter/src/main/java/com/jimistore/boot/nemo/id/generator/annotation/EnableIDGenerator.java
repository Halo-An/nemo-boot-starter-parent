package com.jimistore.boot.nemo.id.generator.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.id.generator.config.NemoIdGeneratorAutoConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(NemoIdGeneratorAutoConfiguration.class)
public @interface EnableIDGenerator {

}

package com.jimistore.boot.nemo.fuse.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.fuse.config.NemoFuseAutoConfiguration;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(NemoFuseAutoConfiguration.class)
public @interface EnableFuse {

}

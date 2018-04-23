package com.jimistore.boot.nemo.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.security.config.NemoSecurityAutoConfiguration;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({NemoSecurityAutoConfiguration.class})
public @interface EnableSecurity {

}

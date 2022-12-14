package com.jimistore.boot.nemo.dao.hibernate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.jimistore.boot.nemo.dao.hibernate.config.NemoDaoHibernateAutoConfiguration;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ NemoDaoHibernateAutoConfiguration.class })
public @interface EnableHibernate {

	String[] daoScanPackage() default { "com.jimistore" };

}

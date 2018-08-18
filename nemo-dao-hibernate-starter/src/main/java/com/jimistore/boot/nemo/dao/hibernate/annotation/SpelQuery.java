package com.jimistore.boot.nemo.dao.hibernate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.jimistore.boot.nemo.dao.hibernate.enums.QueryType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpelQuery {
	
	String value() default "\"\"";
	
	String pageSize() default "#pageSize==null?10:#pageSize";
	
	String pageNum() default "#pageNum==null?1:#pageNum";
	
	QueryType type() default QueryType.HQL;

}

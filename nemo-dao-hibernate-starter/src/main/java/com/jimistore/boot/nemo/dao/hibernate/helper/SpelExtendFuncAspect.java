package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.dao.hibernate.validator.IInjectSqlValidator;

@Aspect
@Order(Integer.MAX_VALUE)
public class SpelExtendFuncAspect {

	private final Logger log = LoggerFactory.getLogger(getClass());

	List<IInjectSqlValidator> queryValidatorList;

	public SpelExtendFuncAspect setQueryValidatorList(List<IInjectSqlValidator> queryValidatorList) {
		this.queryValidatorList = queryValidatorList;
		return this;
	}

	@Pointcut("this(com.jimistore.boot.nemo.dao.hibernate.helper.ISpelExtendFunc)")
	public void spel() {
	}

	@Before("spel()")
	public void before(JoinPoint joinPoint) throws Throwable {
		log.debug("request format check");
		for (IInjectSqlValidator injectSqlValidator : queryValidatorList) {
			Object[] params = joinPoint.getArgs();
			for (int i = 1; params != null && i < params.length; i++) {
				injectSqlValidator.check(params[i]);
			}
		}
	}

}

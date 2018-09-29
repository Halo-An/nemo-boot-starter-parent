package com.jimistore.boot.nemo.core.helper;

import javax.validation.Validator;

import org.aopalliance.aop.Advice;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

public class NemoMethodValidationPostProcessor extends MethodValidationPostProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Advice createMethodValidationAdvice(Validator validator) {
		return (validator != null ? new NemoMethodValidationInterceptor(validator) : new NemoMethodValidationInterceptor());
	}

}

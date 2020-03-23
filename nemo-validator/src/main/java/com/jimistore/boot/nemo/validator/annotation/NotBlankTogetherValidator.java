package com.jimistore.boot.nemo.validator.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

public class NotBlankTogetherValidator implements ConstraintValidator<NotBlankTogether, String> {

	ThreadLocal<Map<String, Map<String, String>>> tl = new ThreadLocal<Map<String, Map<String, String>>>();

	private NotBlankTogether notBlankTogether;

	@Override
	public void initialize(NotBlankTogether constraintAnnotation) {
		this.notBlankTogether = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		ConstraintValidatorContextImpl con = (ConstraintValidatorContextImpl) context;

		Map<String, Map<String, String>> map = tl.get();
		if (map == null) {
			map = new HashMap<String, Map<String, String>>();
			tl.set(map);
		}
		String group = notBlankTogether.value();
		Map<String, String> params = map.get(group);
		if (params == null) {
			params = new HashMap<String, String>();
			map.put(group, params);
		}
		String key = con.getConstraintViolationCreationContexts().get(0).getPath().asString();
		params.put(key, value);

		if (params.size() < notBlankTogether.size()) {
			return true;
		}

		try {
			for (Entry<String, String> entry : params.entrySet()) {
				String val = entry.getValue();
				if (val != null && !val.isEmpty()) {
					return true;
				}
			}
			return false;

		} finally {
			map.remove(group);
		}
	}

}
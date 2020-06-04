package com.jimistore.boot.nemo.validator.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import com.jimistore.boot.nemo.validator.annotation.NotBlankTogether;

public class NotBlankTogetherValidator implements ConstraintValidator<NotBlankTogether, Object> {

	ThreadLocal<Map<String, Map<String, Object>>> tl = new ThreadLocal<Map<String, Map<String, Object>>>();

	private NotBlankTogether notBlankTogether;

	@Override
	public void initialize(NotBlankTogether constraintAnnotation) {
		this.notBlankTogether = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		ConstraintValidatorContextImpl con = (ConstraintValidatorContextImpl) context;

		Map<String, Map<String, Object>> map = tl.get();
		if (map == null) {
			map = new HashMap<String, Map<String, Object>>();
			tl.set(map);
		}
		String group = notBlankTogether.value();
		Map<String, Object> params = map.get(group);
		if (params == null) {
			params = new HashMap<String, Object>();
			map.put(group, params);
		}
		String key = con.getConstraintViolationCreationContexts().get(0).getPath().asString();
		params.put(key, value);

		if (params.size() < notBlankTogether.size()) {
			return true;
		}

		try {
			for (Entry<String, Object> entry : params.entrySet()) {
				String val = entry.getValue().toString();
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
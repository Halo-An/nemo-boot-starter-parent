package com.jimistore.boot.nemo.validator.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import com.jimistore.boot.nemo.validator.annotation.NotSame;

public class NotSameValidator implements ConstraintValidator<NotSame, String> {

	ThreadLocal<Map<String, Map<String, String>>> tl = new ThreadLocal<Map<String, Map<String, String>>>();

	private NotSame notSame;

	@Override
	public void initialize(NotSame constraintAnnotation) {
		this.notSame = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		ConstraintValidatorContextImpl con = (ConstraintValidatorContextImpl) context;

		Map<String, Map<String, String>> map = tl.get();
		if (map == null) {
			map = new HashMap<String, Map<String, String>>();
			tl.set(map);
		}
		String group = notSame.value();
		Map<String, String> params = map.get(group);
		if (params == null) {
			params = new HashMap<String, String>();
			map.put(group, params);
		}
		String key = con.getConstraintViolationCreationContexts().get(0).getPath().asString();
		params.put(key, value);

		if (params.size() < notSame.size()) {
			return true;
		}

		try {
			String old = null;
			boolean first = true;
			for (Entry<String, String> entry : params.entrySet()) {
				String val = entry.getValue();
				if (first || (old == null && val == null) || (old != null && val != null && old.equals(val))) {
					first = false;
					old = val;
					continue;
				}
				return false;
			}
			return true;

		} finally {
			map.remove(group);
		}
	}

}
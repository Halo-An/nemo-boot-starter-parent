package com.jimistore.boot.nemo.validator.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.jimistore.boot.nemo.validator.annotation.EnumValue;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

	private EnumValue enumValue;

	@Override
	public void initialize(EnumValue constraintAnnotation) {
		this.enumValue = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		String[] enumValues = enumValue.value();
		for (String enumValue : enumValues) {
			if (enumValue.equals(value)) {
				return true;
			}
		}
		return false;

	}

}
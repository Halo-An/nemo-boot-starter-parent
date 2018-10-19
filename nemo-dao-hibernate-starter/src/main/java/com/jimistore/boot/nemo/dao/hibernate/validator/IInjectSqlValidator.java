package com.jimistore.boot.nemo.dao.hibernate.validator;

import com.jimistore.boot.nemo.dao.api.exception.QueryValidatorException;
import com.jimistore.boot.nemo.dao.api.validator.IQueryValidator;

public interface IInjectSqlValidator extends IQueryValidator {
	
	public void check(Object...objects) throws QueryValidatorException;

}

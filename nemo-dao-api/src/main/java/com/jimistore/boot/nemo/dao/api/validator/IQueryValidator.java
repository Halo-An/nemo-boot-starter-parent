package com.jimistore.boot.nemo.dao.api.validator;

import com.jimistore.boot.nemo.dao.api.exception.QueryValidatorException;
import com.jimistore.boot.nemo.dao.api.request.Query;

public interface IQueryValidator {
	
	public void check(Query query) throws QueryValidatorException;

}

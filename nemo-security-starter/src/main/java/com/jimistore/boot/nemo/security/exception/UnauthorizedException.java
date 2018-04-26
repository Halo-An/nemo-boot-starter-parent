package com.jimistore.boot.nemo.security.exception;

import com.jimistore.boot.nemo.core.api.exception.BaseException;

public class UnauthorizedException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ERROR_CODE="401";
	
	private static final String ERROR_MSG="接口鉴权异常";

	public UnauthorizedException(Throwable cause) {
		super(ERROR_CODE, String.format("%s==>%s", ERROR_MSG, cause.getMessage()), cause);
	}

	public UnauthorizedException(String message) {
		super(ERROR_CODE, String.format("%s:%s", ERROR_MSG, message));
	}

	public UnauthorizedException() {
		super(ERROR_CODE, ERROR_MSG);
	}
	
	

}

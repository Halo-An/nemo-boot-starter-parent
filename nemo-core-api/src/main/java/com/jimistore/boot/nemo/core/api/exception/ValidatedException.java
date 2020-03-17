package com.jimistore.boot.nemo.core.api.exception;

public class ValidatedException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String CODE = "415";
	private static final String MSG = "参数校验异常";

	public ValidatedException(String message) {
		super(CODE, String.format("%s：%s", MSG, message));
	}

	public ValidatedException() {
		super(CODE, MSG);
	}

	public ValidatedException(String message, Throwable ex) {
		super(CODE, String.format("%s：%s", MSG, message), ex);
	}
}
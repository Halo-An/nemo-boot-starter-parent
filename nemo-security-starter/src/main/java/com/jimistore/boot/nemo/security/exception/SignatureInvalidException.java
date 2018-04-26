package com.jimistore.boot.nemo.security.exception;

public class SignatureInvalidException extends UnauthorizedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private static final String ERROR_MSG="接口调用鉴权签名校验异常";

	public SignatureInvalidException(Throwable cause) {
		super(cause);
	}

	public SignatureInvalidException(String message) {
		super(String.format("%s==>%s", ERROR_MSG, message));
	}

	public SignatureInvalidException() {
		super(ERROR_MSG);
	}
	
	

}

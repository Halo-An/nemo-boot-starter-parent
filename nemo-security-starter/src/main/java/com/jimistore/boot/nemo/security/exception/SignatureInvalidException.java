package com.jimistore.boot.nemo.security.exception;

public class SignatureInvalidException extends UnauthorizedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ERROR_CODE="40102";
		
	private static final String ERROR_MSG="接口调用鉴权签名校验异常";

	public SignatureInvalidException(Throwable cause) {
		super(cause);
		super.setCode(ERROR_CODE);
	}

	public SignatureInvalidException(String message) {
		super(message);
		super.setCode(ERROR_CODE);
	}

	public SignatureInvalidException() {
		super(ERROR_MSG);
		super.setCode(ERROR_CODE);
	}
	
	

}

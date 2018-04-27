package com.jimistore.boot.nemo.security.exception;

public class TokenInvalidException extends UnauthorizedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ERROR_CODE="40101";
		
	private static final String ERROR_MSG="接口调用鉴权令牌校验异常";

	public TokenInvalidException(Throwable cause) {
		super(cause);
		super.setCode(ERROR_CODE);
	}

	public TokenInvalidException(String message) {
		super(String.format("%s==>%s", ERROR_MSG, message));
		super.setCode(ERROR_CODE);
	}

	public TokenInvalidException() {
		super(ERROR_MSG);
		super.setCode(ERROR_CODE);
	}
	
	

}

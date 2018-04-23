package com.jimistore.boot.nemo.security.api.exception;

public class TokenInvalidException extends UnauthorizedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private static final String ERROR_MSG="接口调用鉴权令牌校验异常";

	public TokenInvalidException(Throwable cause) {
		super(cause);
	}

	public TokenInvalidException(String message) {
		super(String.format("%s==>%s", ERROR_MSG, message));
	}

	public TokenInvalidException() {
		super(ERROR_MSG);
	}
	
	

}

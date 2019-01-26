package com.jimistore.boot.nemo.fuse.exception;

import com.jimistore.boot.nemo.core.api.exception.BaseException;

/**
 * 熔断器异常
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public class FuseException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CODE = "5000000";
	
	private static final String MSG = "熔断器异常";

	public FuseException() {
		super(CODE, MSG);
	}

	public FuseException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	public FuseException(String message) {
		super(CODE, message);
	}

	public FuseException(String code, String message, Throwable cause) {
		super(code, message, cause);
		// TODO Auto-generated constructor stub
	}

	public FuseException(String code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}
	
	

}

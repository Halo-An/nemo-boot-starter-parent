package com.jimistore.boot.nemo.fuse.exception;

/**
 * 熔断器超时异常
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public class TimeOutException extends FuseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CODE = "5000004";
	
	private static final String MSG = "熔断器超时异常";

	public TimeOutException() {
		super(CODE, MSG);
	}

	public TimeOutException(String message, Throwable cause) {
		super(CODE, String.format("%s:%s", MSG, message), cause);
	}

	public TimeOutException(String message) {
		super(CODE, String.format("%s:%s", MSG, message));
	}
}

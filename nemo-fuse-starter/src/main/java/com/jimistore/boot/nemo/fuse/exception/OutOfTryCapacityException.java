package com.jimistore.boot.nemo.fuse.exception;

/**
 * 熔断器超出容量异常
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public class OutOfTryCapacityException extends FuseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CODE = "5000003";
	
	private static final String MSG = "熔断器超出容量异常";

	public OutOfTryCapacityException() {
		super(CODE, MSG);
	}

	public OutOfTryCapacityException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

	public OutOfTryCapacityException(String message) {
		super(CODE, message);
	}
}

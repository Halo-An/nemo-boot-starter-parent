package com.jimistore.boot.nemo.fuse.exception;

/**
 * 熔断器超出容量异常
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public class OutOfCapacityException extends FuseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CODE = "5000002";
	
	private static final String MSG = "熔断器超出容量异常";

	public OutOfCapacityException() {
		super(CODE, MSG);
	}

	public OutOfCapacityException(String message, Throwable cause) {
		super(CODE, String.format("%s:%s", MSG, message), cause);
	}

	public OutOfCapacityException(String message) {
		super(CODE, String.format("%s:%s", MSG, message));
	}
}

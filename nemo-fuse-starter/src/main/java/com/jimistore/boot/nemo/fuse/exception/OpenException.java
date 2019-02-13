package com.jimistore.boot.nemo.fuse.exception;

/**
 * 熔断器断开异常
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public class OpenException extends FuseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CODE = "5000001";
	
	private static final String MSG = "熔断器断开异常";

	public OpenException() {
		super(CODE, MSG);
	}

	public OpenException(String message, Throwable cause) {
		super(CODE, String.format("%s:%s", MSG, message), cause);
	}

	public OpenException(String message) {
		super(CODE, String.format("%s:%s", MSG, message));
	}
}

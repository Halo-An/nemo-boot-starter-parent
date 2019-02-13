package com.jimistore.boot.nemo.fuse.exception;

/**
 * 熔断器超时异常
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public class TaskInternalException extends FuseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String CODE = "5000003";
	
	private static final String MSG = "熔断器任务内部异常";

	public TaskInternalException() {
		super(CODE, MSG);
	}

	public TaskInternalException(String message, Throwable cause) {
		super(CODE, String.format("%s:%s", MSG, message), cause);
	}

	public TaskInternalException(String message) {
		super(CODE, String.format("%s:%s", MSG, message));
	}
}

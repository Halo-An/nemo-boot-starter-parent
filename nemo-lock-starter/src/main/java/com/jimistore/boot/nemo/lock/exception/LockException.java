package com.jimistore.boot.nemo.lock.exception;

import com.jimistore.boot.nemo.core.api.exception.BaseException;

public class LockException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ERROR_CODE="502";
	
	private static final String ERROR_MSG="竞争资源锁异常";

	public LockException(Throwable cause) {
		super(ERROR_CODE, String.format("%s:%s", ERROR_MSG, cause.getMessage()), cause);
	}

	public LockException(String message) {
		super(ERROR_CODE, String.format("%s:%s", ERROR_MSG, message));
	}

	public LockException() {
		super(ERROR_CODE, ERROR_MSG);
	}
	
}

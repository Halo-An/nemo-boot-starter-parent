package com.jimistore.boot.nemo.lock.exception;

import com.jimistore.boot.nemo.core.api.exception.BaseException;

public class StockConsumeException extends BaseException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String ERROR_CODE="502";
	
	private static final String ERROR_MSG="竞争消费锁异常";

	public StockConsumeException(Throwable cause) {
		super(ERROR_CODE, String.format("%s:%s", ERROR_MSG, cause.getMessage()), cause);
	}

	public StockConsumeException(String message) {
		super(ERROR_CODE, String.format("%s:%s", ERROR_MSG, message));
	}

	public StockConsumeException() {
		super(ERROR_CODE, ERROR_MSG);
	}
	
}

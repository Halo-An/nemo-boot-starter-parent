package com.jimistore.boot.nemo.high.concurrency.api.exception;

public class StockConsumeException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code="503";

	public StockConsumeException(String code,String message,Throwable cause) {
		super(message,cause);
		this.code=code;
	}


	public StockConsumeException(String code,String message) {
		super(message);
		this.code=code;
	}
	
	public StockConsumeException(String message,Throwable cause) {
		super(message,cause);
	}

	public StockConsumeException(String message) {
		super(message);
	}

	public StockConsumeException() {
		super("lock failed");
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
	
}

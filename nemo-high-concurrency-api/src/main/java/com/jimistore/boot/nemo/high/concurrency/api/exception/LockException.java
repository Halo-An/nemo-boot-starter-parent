package com.jimistore.boot.nemo.high.concurrency.api.exception;

public class LockException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code="502";

	public LockException(String code,String message,Throwable cause) {
		super(message,cause);
		this.code=code;
	}


	public LockException(String code,String message) {
		super(message);
		this.code=code;
	}
	
	public LockException(String message,Throwable cause) {
		super(message,cause);
	}

	public LockException(String message) {
		super(message);
	}

	public LockException() {
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

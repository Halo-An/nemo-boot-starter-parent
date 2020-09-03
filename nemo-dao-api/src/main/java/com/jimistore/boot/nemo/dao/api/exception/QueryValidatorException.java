package com.jimistore.boot.nemo.dao.api.exception;

public class QueryValidatorException extends DaoException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code = "41501";

	private static final String msg = "疑似sql注入攻击";

	public QueryValidatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryValidatorException(String message) {
		super(String.format("%s:%s", msg, message));
	}

	public QueryValidatorException() {
		super(msg);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Throwable fillInStackTrace() {
//		return super.fillInStackTrace();
		// 性能优化300倍
		return this;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(this.getClass().getName()).append("==>").append(this.getMessage()).toString();
	}

}

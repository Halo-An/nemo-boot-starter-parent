package com.jimistore.boot.nemo.security.core;

import javax.servlet.http.HttpServletRequest;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

public interface ISignatureValidator {

	/**
	 * 是否匹配
	 * 
	 * @return
	 */
	public boolean isMatch(HttpServletRequest request);

	/**
	 * 校验签名
	 * 
	 * @param request
	 */
	public void check(HttpServletRequest request) throws SignatureInvalidException;

}

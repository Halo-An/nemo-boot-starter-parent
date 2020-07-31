package com.jimistore.boot.nemo.security.helper;

import javax.servlet.http.HttpServletRequest;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

public interface ISignatureValidator {

	/**
	 * 获取签名类型
	 * 
	 * @return
	 */
	public String getSignType();

	/**
	 * 校验签名
	 * 
	 * @param request
	 */
	public void check(HttpServletRequest request) throws SignatureInvalidException;

}

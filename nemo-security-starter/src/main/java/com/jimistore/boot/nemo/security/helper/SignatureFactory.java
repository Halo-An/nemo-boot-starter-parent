package com.jimistore.boot.nemo.security.helper;

import java.util.Map;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

public class SignatureFactory implements ISignatureFactory {

	private static final String SECRET_KEY = "secret";

	private String secret;

	@Override
	public String create(Map<String, Object> param) {
		param.put(SECRET_KEY, secret);
		try {
			return SecurityUtil.SignMD5(param);
		} catch (SignatureInvalidException e) {
			throw new SignatureInvalidException(e);
		}
	}

	@Override
	public void check(Map<String, Object> param, String signature) {
		param.put(SECRET_KEY, secret);
		try {
			String sign = SecurityUtil.SignMD5(param);
			if (!sign.equals(signature)) {
				throw new SignatureInvalidException("签名异常");
			}
		} catch (SignatureInvalidException e) {
			throw new SignatureInvalidException(e);
		}

	}

}

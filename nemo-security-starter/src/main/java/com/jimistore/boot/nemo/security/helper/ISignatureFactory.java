package com.jimistore.boot.nemo.security.helper;

import java.util.Map;

public interface ISignatureFactory {
	
	/**
	 * 生成签名
	 * @param param
	 * @return
	 */
	public String create(Map<String,Object> param);
	
	/**
	 * 校验签名是否正确
	 * @param param
	 * @param signature
	 */
	public void check(Map<String,Object> param, String signature);

}

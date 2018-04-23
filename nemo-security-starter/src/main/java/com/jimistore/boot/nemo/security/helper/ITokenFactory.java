package com.jimistore.boot.nemo.security.helper;

import com.jimistore.boot.nemo.security.api.exception.TokenInvalidException;

public interface ITokenFactory {
	
	/**
	 * 创建一个
	 * @param userId
	 * @param deviceId
	 * @return tocken
	 */
	public String create(String userId, String deviceId);
	
	/**
	 * 校验tocken是否有效且是否涉及越权
	 * @param userId
	 * @param deviceId
	 * @param tocken
	 * @throws 校验失败异常
	 */
	public void check(String userId, String deviceId, String tocken) throws TokenInvalidException;

}

package com.jimistore.boot.nemo.security.helper;

import java.util.Map;

import com.jimistore.boot.nemo.security.exception.TokenInvalidException;

public interface ITokenFactory {
	
	/**
	 * 创建一个
	 * @param userId 用户标识
	 * @param deviceId 设备标识
	 * @return tocken
	 */
	public String create(String userId, String deviceId);
	
	/**
	 * 创建一个
	 * @param appid 系统标识
	 * @param userId 用户标识
	 * @param deviceId 设备标识
	 * @param extend 扩展字段
	 * @param timeout 超时时间(ms)
	 * @return tocken
	 */
	public String create(String appid, String userId, String deviceId, int timeout);
	
	/**
	 * 创建一个
	 * @param appid 系统标识
	 * @param userId 用户标识
	 * @param deviceId 设备标识
	 * @param extend 扩展字段
	 * @param timeout 超时时间(ms)
	 * @return tocken
	 */
	public String create(String appid, String userId, String deviceId, Map<String, String> extend, int timeout);
	
	/**
	 * 校验tocken是否有效且是否涉及越权
	 * @param userId
	 * @param deviceId
	 * @param tocken
	 * @throws 校验失败异常
	 */
	public void check(String userId, String deviceId, String tocken) throws TokenInvalidException;

}

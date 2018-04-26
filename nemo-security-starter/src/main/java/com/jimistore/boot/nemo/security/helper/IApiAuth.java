package com.jimistore.boot.nemo.security.helper;

public interface IApiAuth {
	
	/**
	 * 获取appid对应的密码
	 * @param appid
	 * @return
	 */
	public String getSecret(String appid);
	
	/**
	 * 获取授权appid访问的url通配符
	 * @return
	 */
	public String[] getMatch(String appid);
	
	/**
	 * 忽略验证的url通配符
	 * @return
	 */
	public String[] getIgnoreMatch();
	
	/**
	 * 判断是否为空
	 * @return
	 */
	public boolean isEmpty();
}
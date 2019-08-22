package com.jimistore.boot.nemo.monitor.server.helper;

import java.util.Set;

/**
 * 监控报警白名单
 * 
 * @author chenqi
 * @date 2019年6月4日
 *
 */
public interface IWhiteStorage {

	/**
	 * 获取所有报名单
	 * 
	 * @return
	 */
	public Set<String> getWhiteServiceSet();

}

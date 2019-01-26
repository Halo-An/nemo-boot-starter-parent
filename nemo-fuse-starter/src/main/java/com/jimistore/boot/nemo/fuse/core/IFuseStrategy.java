package com.jimistore.boot.nemo.fuse.core;

import com.jimistore.boot.nemo.fuse.enums.FuseState;

/**
 * 熔断器策略接口
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public interface IFuseStrategy {
	
	/**
	 * 熔断器调用统计信息变更时
	 * @param fuse
	 */
	public FuseState changeInfo(IFuse fuse);

}

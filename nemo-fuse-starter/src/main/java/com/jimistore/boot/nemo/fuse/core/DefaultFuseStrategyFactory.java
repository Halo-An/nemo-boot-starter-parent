package com.jimistore.boot.nemo.fuse.core;

import java.util.List;

/**
 * 熔断器策略工厂默认实现（只支持一个策略）
 * @author chenqi
 * @date 2019年1月30日
 *
 */
public class DefaultFuseStrategyFactory implements IFuseStrategyFactory {
	
	private List<IFuseStrategy> fuseStrategyList;

	public DefaultFuseStrategyFactory setFuseStrategyList(List<IFuseStrategy> fuseStrategyList) {
		this.fuseStrategyList = fuseStrategyList;
		return this;
	}

	@Override
	public IFuseStrategy gennerator(String key) {
		return fuseStrategyList.get(0);
	}

}

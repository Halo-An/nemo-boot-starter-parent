package com.jimistore.boot.nemo.fuse.core;

/**
 * 熔断器策略工厂接口
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public interface IFuseStrategyFactory {
	
	/**
	 * 生成熔断器策略
	 * @param key
	 * @return
	 */
	public IFuseStrategy gennerator(String key);

}

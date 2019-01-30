package com.jimistore.boot.nemo.fuse.core;

/**
 * 熔断器策略接口
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public interface IFuseStrategy {
	
	/**
	 * 熔断器被创建时
	 * @param fuseInfo
	 */
	public void creating(IFuseInfo fuseInfo);
	
	/**
	 * 熔断器执行任务前
	 * @param fuseInfo
	 * @return
	 */
	public void executeBefore(IFuseInfo fuseInfo);
	
	/**
	 * 熔断器执行任务成功
	 * @param fuseInfo
	 * @return
	 */
	public void executeSuccess(IFuseInfo fuseInfo);
	
	/**
	 * 熔断器执行任务异常
	 * @param fuseInfo
	 * @return
	 */
	public void executeException(IFuseInfo fuseInfo, Throwable throwable);

}

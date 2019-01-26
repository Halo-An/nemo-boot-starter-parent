package com.jimistore.boot.nemo.fuse.core;

import com.jimistore.boot.nemo.fuse.enums.FuseState;
import com.jimistore.boot.nemo.fuse.exception.OpenException;
import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.OutOfTryCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 熔断器
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public interface IFuse {
	
	/**
	 * 获取熔断器当前状态
	 * @return
	 */
	public FuseState getState();
	
	/**
	 * 获取熔断器的标识
	 * @return
	 */
	public String getKey();
	
	/**
	 * 获取熔断器信息
	 * @return
	 */
	public IFuseInfo getFuseInfo();
	
	/**
	 * 执行任务
	 * @param task
	 * @return
	 * @throws 
	 */
	public <V> V execute(ITask<V> task) throws TimeOutException, OpenException, OutOfCapacityException, OutOfTryCapacityException;

}

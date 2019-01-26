package com.jimistore.boot.nemo.fuse.core;

import java.util.List;

import com.jimistore.boot.nemo.fuse.exception.OpenException;
import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.OutOfTryCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 熔断器容器接口
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public interface IFuseContainer {

	/**
	 * 执行任务
	 * @param key 熔断器标识
	 * @param task 任务
	 * @return
	 * @throws 
	 */
	public <V> V execute(String key, ITask<V> task) throws TimeOutException, OpenException, OutOfCapacityException, OutOfTryCapacityException;

	/**
	 * 获取所有熔断器信息
	 * @return
	 */
	public List<IFuseInfo> getFuseInfoList(); 
	
}

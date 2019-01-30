package com.jimistore.boot.nemo.fuse.core;

import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TaskInternalException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 任务执行器接口
 * @author chenqi
 * @date 2019年1月25日
 *
 */
public interface IFuseExecutor {
	
	/**
	 * 执行任务
	 * @param task
	 * @return
	 */
	public <V> V execute(ITask<V> task) throws TimeOutException, OutOfCapacityException, TaskInternalException;

}

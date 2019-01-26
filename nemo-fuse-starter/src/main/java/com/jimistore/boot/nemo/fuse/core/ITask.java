package com.jimistore.boot.nemo.fuse.core;

import java.util.concurrent.Callable;

/**
 * 熔断器任务接口
 * @author chenqi
 * @date 2019年1月25日
 *
 * @param <V>
 */
public interface ITask<V> extends Callable<V> {

	/**
	 * 获取任务执行超时时间
	 * @return
	 */
	public long getTimeout();
	
}

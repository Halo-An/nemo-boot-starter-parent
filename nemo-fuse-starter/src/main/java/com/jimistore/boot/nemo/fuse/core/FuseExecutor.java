package com.jimistore.boot.nemo.fuse.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.jimistore.boot.nemo.fuse.config.FuseProperties;
import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TaskInternalException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 任务执行器
 * @author chenqi
 * @date 2019年1月28日
 *
 */
public class FuseExecutor implements IFuseExecutor {
	
	FuseProperties fuseProperties;
	
	ExecutorService executorService;

	public FuseExecutor(FuseProperties fuseProperties) {
		super();
		this.fuseProperties = fuseProperties;
		executorService = Executors.newFixedThreadPool(fuseProperties.getMaxExecutorThreadSize());
	}

	@Override
	public <V> V execute(ITask<V> task) throws TimeOutException, OutOfCapacityException, TaskInternalException {
		FutureTask<V> futureTask = new FutureTask<V>(task);
		executorService.execute(futureTask);
		try {
			return futureTask.get(task.getTimeout(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		} catch (TimeoutException e) {
			futureTask.cancel(true);
			throw new TimeOutException();
		} catch (Exception e) {
			throw new TaskInternalException(e.getMessage(), e);
		}
	}

}

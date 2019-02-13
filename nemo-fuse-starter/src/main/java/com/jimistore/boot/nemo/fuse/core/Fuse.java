package com.jimistore.boot.nemo.fuse.core;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.fuse.enums.FuseState;
import com.jimistore.boot.nemo.fuse.exception.OpenException;
import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TaskInternalException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 熔断器默认实现
 * @author chenqi
 * @date 2019年1月26日
 *
 */
public class Fuse implements IFuse {
	
	private static final Logger log = Logger.getLogger(Fuse.class);
	
	private IFuseStrategy fuseStrategy;
	
	private IFuseExecutor fuseExecutor;
	
	private IFuseInfo fuseInfo;

	public Fuse setFuseStrategy(IFuseStrategy fuseStrategy) {
		this.fuseStrategy = fuseStrategy;
		return this;
	}

	public Fuse setFuseExecutor(IFuseExecutor fuseExecutor) {
		this.fuseExecutor = fuseExecutor;
		return this;
	}

	@Override
	public FuseState getState() {
		return this.fuseInfo.getFuseState();
	}

	@Override
	public String getKey() {
		return this.getFuseInfo().getKey();
	}

	@Override
	public IFuseInfo getFuseInfo() {
		// TODO Auto-generated method stub
		return fuseInfo;
	}

	public Fuse setFuseInfo(IFuseInfo fuseInfo) {
		this.fuseInfo = fuseInfo;
		return this;
	}

	@Override
	public <V> V execute(ITask<V> task)
			throws TimeOutException, OpenException, OutOfCapacityException, TaskInternalException {
		if(log.isDebugEnabled()) {
			log.debug(String.format("request execute, key is %s, timeout is %s", this.getKey(), task.getTimeout()));
		}
		FuseState fuseState = fuseInfo.getFuseState();

		if(!fuseState.isAvailable()) {
			throw new OpenException(fuseInfo.getKey());
		}
		if(fuseState.equals(FuseState.TRY)) {
			if(fuseInfo instanceof FuseInfo) {
				((FuseInfo)fuseInfo).setFuseState(FuseState.TRYING);
			}
		}
		try {
			return fuseExecutor.execute(task);
		}catch(TimeOutException e) {
			throw new TimeOutException(fuseInfo.getKey());
		}
	}

	public IFuseStrategy getFuseStrategy() {
		return fuseStrategy;
	}

}

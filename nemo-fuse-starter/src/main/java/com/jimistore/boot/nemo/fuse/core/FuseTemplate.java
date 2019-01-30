package com.jimistore.boot.nemo.fuse.core;

import java.util.List;

import com.jimistore.boot.nemo.fuse.config.FuseProperties;
import com.jimistore.boot.nemo.fuse.exception.OpenException;
import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TaskInternalException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 快速使用的模板类
 * @author chenqi
 * @date 2019年1月30日
 *
 */
public class FuseTemplate implements IFuseContainer {
	
	FuseProperties fuseProperties;
	
	IFuseContainer fuseContainer;
	
	private FuseTemplate() {
		
	}
	
	

	public FuseProperties getFuseProperties() {
		return fuseProperties;
	}



	public FuseTemplate setFuseProperties(FuseProperties fuseProperties) {
		this.fuseProperties = fuseProperties;
		return this;
	}



	public IFuseContainer getFuseContainer() {
		return fuseContainer;
	}



	public FuseTemplate setFuseContainer(IFuseContainer fuseContainer) {
		this.fuseContainer = fuseContainer;
		return this;
	}
	
	public static FuseTemplate create(FuseProperties fuseProperties, IFuseStrategyFactory fuseStrategyFactory) {
		FuseTemplate fuseTemplate = new FuseTemplate()
				.setFuseProperties(fuseProperties)
				.setFuseContainer(new FuseContainer()
						.setFuseExecutor(new FuseExecutor(fuseProperties))
						.setFuseStrategyFactory(fuseStrategyFactory));
		return fuseTemplate;
	}



	@Override
	public <V> V execute(String key, ITask<V> task)
			throws TimeOutException, OpenException, OutOfCapacityException, TaskInternalException {
		return fuseContainer.execute(key, task);
	}

	@Override
	public List<IFuseInfo> getFuseInfoList() {
		return fuseContainer.getFuseInfoList();
	}

}

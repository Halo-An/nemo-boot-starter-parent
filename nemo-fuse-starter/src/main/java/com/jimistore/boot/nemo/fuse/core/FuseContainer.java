package com.jimistore.boot.nemo.fuse.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.jimistore.boot.nemo.fuse.exception.FuseException;
import com.jimistore.boot.nemo.fuse.exception.OpenException;
import com.jimistore.boot.nemo.fuse.exception.OutOfCapacityException;
import com.jimistore.boot.nemo.fuse.exception.TaskInternalException;
import com.jimistore.boot.nemo.fuse.exception.TimeOutException;

/**
 * 
 * @author chenqi
 * @date 2019年1月30日
 *
 */
public class FuseContainer implements IFuseContainer {
	
	private static final Logger log = Logger.getLogger(FuseContainer.class);
	
	private ConcurrentHashMap<String, IFuse> fuseMap = new ConcurrentHashMap<String, IFuse>();
	
	private IFuseStrategyFactory fuseStrategyFactory;
	
	private IFuseExecutor fuseExecutor;

	public FuseContainer setFuseStrategyFactory(IFuseStrategyFactory fuseStrategyFactory) {
		this.fuseStrategyFactory = fuseStrategyFactory;
		return this;
	}

	public FuseContainer setFuseExecutor(IFuseExecutor fuseExecutor) {
		this.fuseExecutor = fuseExecutor;
		return this;
	}

	@Override
	public <V> V execute(String key, ITask<V> task) throws TimeOutException, OpenException, OutOfCapacityException, TaskInternalException {
		if(log.isDebugEnabled()) {
			log.debug(String.format("request execute, the key is %s, the timeout is %s", key, task.getTimeout()));
		}
		IFuse fuse = fuseMap.get(key);
		if(fuse == null) {
			if(log.isDebugEnabled()) {
				log.debug(String.format("create fuse, the key is %s, the timeout is %s", key, task.getTimeout()));
			}
			//根据key生成策略
			IFuseStrategy fuseStrategy = fuseStrategyFactory.gennerator(key);

			if(log.isDebugEnabled()) {
				log.debug(String.format("create fuse, the key is %s, the timeout is %s， the strategy's class is %s", key, task.getTimeout(), fuseStrategy.getClass()));
			}
			
			IFuseInfo fuseInfo = new FuseInfo().setKey(key);
			//创建熔断器
			fuse = new Fuse()
					.setFuseInfo(fuseInfo)
					.setFuseExecutor(fuseExecutor)
					.setFuseStrategy(fuseStrategy);
			IFuse old = fuseMap.putIfAbsent(key, fuse);
			
			//如果有线程资源没有冲突
			if(old == null) {
				fuseStrategy.creating(fuseInfo);
			}
		}
		
		Throwable throwable = null;
		try {
			fuse.getFuseStrategy().executeBefore(fuse.getFuseInfo());
			return fuse.execute(task);
		}catch(Exception e){
			throwable = e;
			//如果声明了异常回调，则调用回调函数，并使用回调函数的结果作为返回
			if(e instanceof FuseException && task.getCallback()!=null) {
				try {
					return task.getCallback().call();
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}
			throw e;
		}finally {
			if(throwable==null) {
				fuse.getFuseStrategy().executeSuccess(fuse.getFuseInfo());
			}else {
				fuse.getFuseStrategy().executeException(fuse.getFuseInfo(), throwable);
			}
		}
	}

	@Override
	public List<IFuseInfo> getFuseInfoList() {
		List<IFuseInfo> infoList = new ArrayList<IFuseInfo>();
		Collection<IFuse> fuseList = fuseMap.values();
		for(IFuse fuse:fuseList) {
			infoList.add(fuse.getFuseInfo());
		}
		return infoList;
	}

}

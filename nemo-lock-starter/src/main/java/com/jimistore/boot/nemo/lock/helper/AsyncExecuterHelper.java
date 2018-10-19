package com.jimistore.boot.nemo.lock.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;

public class AsyncExecuterHelper {
	
	Map<String, AsyncExecuter> map = new HashMap<String, AsyncExecuter>();
	
	public void execute(String name, int capacity, IExecuter executer){
		AsyncExecuter asyncExecuter = null;
		synchronized (map) {
			if(!map.containsKey(name)){
				map.put(name, new AsyncExecuter(capacity, name));
			}
			asyncExecuter = map.get(name);
		}
		asyncExecuter.execute(executer);
	}
	
	Map<String, ThreadPoolExecutor> commandMap = new HashMap<String, ThreadPoolExecutor>();
	
	public void execute(String name, int capacity, int maxCapacity, int queueCapacity, Runnable command){
		
		ThreadPoolExecutor threadPoolExecutor = null;
		synchronized (commandMap) {
			threadPoolExecutor = commandMap.get(name);
			if(threadPoolExecutor==null){
				threadPoolExecutor = new ThreadPoolExecutor(capacity, maxCapacity, 
						3000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(queueCapacity), 
						Executors.defaultThreadFactory(), new CallerRunsPolicy());
				commandMap.put(name, threadPoolExecutor);
			}
		}
		threadPoolExecutor.execute(command);
	}

}

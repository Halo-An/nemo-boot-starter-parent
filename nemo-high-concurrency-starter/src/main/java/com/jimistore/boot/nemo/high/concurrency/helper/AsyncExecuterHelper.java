package com.jimistore.boot.nemo.high.concurrency.helper;

import java.util.HashMap;
import java.util.Map;

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

}

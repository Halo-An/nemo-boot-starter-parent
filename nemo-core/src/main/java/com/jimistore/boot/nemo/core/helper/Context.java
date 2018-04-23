package com.jimistore.boot.nemo.core.helper;

import java.util.HashMap;
import java.util.Map;

public class Context {
	
	private static final ThreadLocal<ContextStore> context=new ThreadLocal<ContextStore>();
	
	/**
	 * 请求用户
	 */
	public static final String CONTEXT_REQUEST_USER = "request-user";
	
	/**
	 * 请求的事务编号
	 */
	public static final String CONTEXT_TRANSITION_ID = "request-transition-id";
	
	/**
	 * 请求的链路编号
	 */
	public static final String CONTEXT_SLEUTH_ID = "request-sleuth-id";
	
	private static class ContextStore{
		Map<String,Object> map=new HashMap<String,Object>();
		public Object get(String key){
			return map.get(key);
		}
		public void put(String key,Object value){
			map.put(key, value);
		}
	}
	
	public static Object get(String key){
		if(context.get()==null){
			Context.init();
		}
		return context.get().get(key);
	}
	
	public static void put(String key,Object value){
		if(context.get()==null){
			Context.init();
		}
		context.get().put(key, value);
	}
	
	public static void init(){
		context.set(new ContextStore());
	}
}

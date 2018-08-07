package com.jimistore.boot.nemo.mq.rocketmq.adapter;

import java.lang.reflect.Method;

import com.cq.nemo.util.reflex.AnnotationUtil;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQService;
import com.jimistore.boot.nemo.mq.core.helper.MQNameHelper;
import com.jimistore.boot.nemo.mq.rocketmq.config.MutilRocketMQProperties;

public class RocketMQNameHelper extends MQNameHelper {
	
	MutilRocketMQProperties mutilRocketMQProperties;
	
	public RocketMQNameHelper setMutilRocketMQProperties(MutilRocketMQProperties mutilRocketMQProperties) {
		this.mutilRocketMQProperties = mutilRocketMQProperties;
		return this;
	}


	@Override
	protected String getDestinationName(Class<?> clazz, Method method) {
		String destName = super.getDestinationName(clazz, method);
		if(destName!=null){
			if(clazz.isInterface()){
				if(clazz.isAnnotationPresent(JsonMQService.class)){
					String alias = this.getAliasByClass(clazz, destName);
					if(alias!=null){
						return alias;
					}
				}
				
			}else{
				for(Class<?> intf:clazz.getInterfaces()){
					if(intf.isAnnotationPresent(JsonMQService.class)){
						String alias = this.getAliasByClass(intf, destName);
						if(alias!=null){
							return alias;
						}
					}
					
				}
				
			}
		}
		
		return destName;
	}
	
	private String getAliasByClass(Class<?> clazz, String destName){
		JsonMQService jsonMQService = AnnotationUtil.getAnnotation(clazz, JsonMQService.class);
		RocketMQProperties rocketMQProperties = mutilRocketMQProperties.getRocketmq().get(jsonMQService.value());
		if(rocketMQProperties!=null){
			String alias = rocketMQProperties.getTopicMap().get(destName);
			if(alias!=null){
				return alias;
			}
		}
		return null;
	}

}

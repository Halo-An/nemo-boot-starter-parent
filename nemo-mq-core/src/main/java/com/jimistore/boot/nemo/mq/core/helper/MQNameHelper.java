package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.Method;

import org.springframework.util.StringUtils;

import com.cq.nemo.util.reflex.AnnotationUtil;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQName;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQService;

/**
 * 用来处理mq名称等统一的工具类
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class MQNameHelper {
	
	private static final String SPLIT = ".";
	private static final String PARAM_SPLIT = "-";
	
	
	public static String getMQGroup(String className, String mQGroup){
		return StringUtils.isEmpty(mQGroup) ? className : mQGroup;
	}
	
	public static String getMQGroupByMQName(String mQName){
		return mQName.substring(0, mQName.lastIndexOf(SPLIT));
	}
	
	public static String getMQNameByGroupAndMethod(String className, String mQGroup, Method method){
		
		JsonMQName jsonMQName = AnnotationUtil.getAnnotation(method, JsonMQName.class);
		if(jsonMQName!=null){
			return jsonMQName.value();
		}
		
		StringBuilder methodId = new StringBuilder(method.getName());
		for(Class<?> clazz:method.getParameterTypes()){
			methodId.append(PARAM_SPLIT).append(clazz.getSimpleName());
		}
		
		return String.format("%s%s%s", MQNameHelper.getMQGroup(className, mQGroup), SPLIT, methodId.toString());	
	}
	
	public static Method getMethodByMQNameAndTarget(String mQName, Class<?>[] paramClasses, Object target) throws NoSuchMethodException, SecurityException {
		String methodName = mQName.substring(mQName.lastIndexOf(SPLIT)+1);
		if(methodName.indexOf(PARAM_SPLIT)>0){
			methodName = methodName.substring(0, methodName.indexOf(PARAM_SPLIT));
		}
		for(Class<?> clazz:target.getClass().getInterfaces()){
			JsonMQService jsonMQService = AnnotationUtil.getAnnotation(clazz, JsonMQService.class);
			if(jsonMQService!=null){
				for(Method method:clazz.getMethods()){
					JsonMQName jsonMQName = AnnotationUtil.getAnnotation(method, JsonMQName.class);
					if(jsonMQName!=null){
						methodName = method.getName();
					}
				}
			}
		}
		return target.getClass().getMethod(methodName, paramClasses);
	}
	

}

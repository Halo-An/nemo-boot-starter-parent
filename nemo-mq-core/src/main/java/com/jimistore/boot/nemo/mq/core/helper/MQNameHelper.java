package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.Method;

import com.jimistore.boot.nemo.core.util.AnnotationUtil;
import com.jimistore.boot.nemo.mq.core.annotation.JsonMQMapping;
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
	
	/**
	 * 根据class和method获取MQName
	 * @param clazz
	 * @param method
	 * @return
	 */
	public String getMQNameClassAndMethod(Class<?> clazz, Method method) {
		
		String destName = this.getDestinationName(clazz, method);
		if(destName!=null){
			return destName;
		}

		StringBuilder methodId = new StringBuilder(method.getName());
		for(Class<?> paramType:method.getParameterTypes()){
			methodId.append(PARAM_SPLIT).append(paramType.getSimpleName());
		}
		
		return String.format("%s%s%s", clazz.getName(), SPLIT, methodId.toString());
	}
	
	protected String getDestinationName(Class<?> clazz, Method method){
		JsonMQMapping destination = AnnotationUtil.getAnnotation(method, JsonMQMapping.class);
		if(destination!=null&&destination.value()!=null&&!destination.value().isEmpty()){
			return destination.value();
		}
		return null;
	}
	
	/**
	 * 根据mqName获取接口的方法
	 * @param intf
	 * @param paramClasses
	 * @param target
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Method getMethodByMQNameAndTarget(String mQName, String tag, Class<?>[] paramClasses, Object target) throws NoSuchMethodException, SecurityException {
		String methodName = null;
		for(Class<?> clazz:target.getClass().getInterfaces()){
			if(clazz.isAnnotationPresent(JsonMQService.class)){
				for(Method method:clazz.getMethods()){
					String destName = this.getDestinationName(clazz, method);
					JsonMQMapping destination = AnnotationUtil.getAnnotation(method, JsonMQMapping.class);
					if(destName!=null&&destName.equals(mQName)&&tag!=null&&tag.equals(destination.tag())){
						methodName = method.getName();
						break;
					}
				}
				break;
			}
		}
		
		if(methodName==null){
			methodName = mQName.substring(mQName.lastIndexOf(SPLIT)+1);
			if(methodName.indexOf(PARAM_SPLIT)>0){
				methodName = methodName.substring(0, methodName.indexOf(PARAM_SPLIT));
			}
		}
		
		return target.getClass().getMethod(methodName, paramClasses); 
	}

	

}

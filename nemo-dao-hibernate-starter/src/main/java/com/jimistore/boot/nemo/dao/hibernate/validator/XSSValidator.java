package com.jimistore.boot.nemo.dao.hibernate.validator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.cq.nemo.util.reflex.ClassUtil;
import com.jimistore.boot.nemo.dao.api.exception.QueryValidatorException;
import com.jimistore.boot.nemo.dao.api.exception.XssValidatorException;
import com.jimistore.boot.nemo.dao.api.validator.IXSSValidator;

public class XSSValidator implements IXSSValidator {
	
	@Value("${xss.replace:}")
	String replace;
	
	/**
	 * 非法字符串
	 */
	public static final String[] errStr={
			"script",
			"link",
			"iframe",
			"img",
			"input",
			"frameset",
			"video",
			"sound",
			"object",
			"embed",
			"bgsound",
			"audio",
			"source",
			"<"
			};

	@Override
	public void check(Object entity) throws XssValidatorException {
		if(entity==null){
			return ;
		}
		List<Field> fieldList = ClassUtil.getFields(entity.getClass());
		for(Field field:fieldList){
			String getMethod = ClassUtil.getGetMethodNameByField(field);
			try {
				Object value = entity.getClass().getMethod(getMethod, new Class[]{}).invoke(entity, new Object[]{});
				if(value instanceof String){
					String str = (String) value;
					int result = checkValue(str);
					if(result>0){
						//替换还是直接抛异常
						if(replace!=null&&replace.length()>0){
							str = str.replaceAll(errStr[result], String.format("%s%s%s", replace, str, replace));
							String setMethod = ClassUtil.getSetMethodNameByField(field);
							entity.getClass().getMethod(setMethod, new Class[]{String.class}).invoke(entity, new Object[]{str});
						}else{
							throw new QueryValidatorException();
						}
						
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 检查命中异常规则第几项
	 * @param value
	 * @return
	 * @throws XssValidatorException
	 */
	public static int checkValue(String value) throws XssValidatorException {
		for(int i=0;i<errStr.length;i++){
			String str = errStr[i];
			if(value.toString().indexOf(str)>=0){
				return i;
			}
		}
		return -1;
	}

}
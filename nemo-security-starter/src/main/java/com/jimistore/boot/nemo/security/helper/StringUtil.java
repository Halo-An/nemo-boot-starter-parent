package com.jimistore.boot.nemo.security.helper;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class StringUtil {
	
	public static final String[] SPLIT_STR = {",",";"};
	
	public static boolean isNotNull(String str){
		return str!=null&&str.length()>0;
	}
	
//	public static final char[] chars=new char[]{
//		'e','s','w','y','2','k',
//		'j','0','c','l','b','o',
//		'n','x','r','a','i','6',
//		'm','g','f','1','8','d',
//		'7','q','h','z','4','5',
//		'3','p','9','v','u','t'
//	};
	
	public static final char[] chars=new char[]{
		'8','9','0','7','2','1',
		'3','5','6','4'
	};
	
	/**
	 * 根据编码长度和索引获取唯一标识
	 * @param size
	 * @param num
	 * @return
	 */
	public static String getKey(long size,long num){
		int length=chars.length;
		long max=(long)Math.pow(length, size);
		long growth=0;
		for(int i=0;i<size;i++){
			growth+=(long)Math.pow(length, i)*(i+1);
		}
		long index=(num*growth)%max;
		StringBuilder str=new StringBuilder();
		for(int i=0;i<size;i++){
			int reach=(int)(index%length);
			str.append(chars[reach]);
			index=index/length;
		}
		
		return str.toString();
	}

	/**
	 * 字符串变量替换
	 * @param source
	 * @param fieldMap
	 * @return
	 */
	public static String replace(String source,Map<String,String> fieldMap){
		if(source==null){
			return "";
		}
		if(fieldMap==null){
			return source;
		}
		Iterator<String> it=fieldMap.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			source=source.replace(new StringBuilder("\\$\\{").append(key).append("\\}").toString(), fieldMap.get(key));
		}
		return source;
	}

	public static String join(Object[] objs){
		return join(objs,";");
	}
	
	/**
	 * 字符串拼接
	 * @param objs
	 * @param joinStr
	 * @return
	 */
	public static String join(Object[] objs , String joinStr){
		if(objs == null){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(Object obj : objs){
			if(sb.length()>0){
				sb.append(joinStr);
			}
			sb.append(obj.toString());
		}
		return sb.toString();
	}
	


    /**
     * 格式化数字不足补齐
     * 
     * @param size
     * @param value
     * @return
     */
    public static String NoFormat(int size, long value) {
        String result = (new Long(value)).toString();
        while (size > result.length()) {
            result = "0" + result;
        }
        return result;
    }
    
    /**
     * 通过spel格式化字符串
     * @param expression
     * @param contextMap
     * @return
     */
    public static String formatBySpel(String expression, Map<Object,Object> contextMap){
    	ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		
		for(Entry<Object,Object> entry:contextMap.entrySet()){
			context.setVariable(entry.getKey().toString(), entry.getValue());
		}
		return parser.parseExpression(expression).getValue(context, String.class);
    }
    
	public static String[] split(String str, String[] splitStrs){
		if(str==null){
			return null;
		}
		String splitStr = ",";
		for(String split:splitStrs){
			str = str.replaceAll(split, splitStr);
		}
		return str.split(splitStr);
	}
	
}

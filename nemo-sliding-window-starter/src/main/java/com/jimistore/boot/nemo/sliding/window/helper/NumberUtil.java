package com.jimistore.boot.nemo.sliding.window.helper;

public class NumberUtil {
	
	public static Number add(Number a, Number b){
		if(a==null||a.equals(0)){
			return b;
		}
		if(b==null||b.equals(0)){
			return a;
		}
		if(a instanceof Double){
			Double c = (Double)a + (Double) b;
			return c;
		}else if(a instanceof Float){
			Float c = (Float)a + (Float) b;
			return c;
		}else if(a instanceof Long){
			Long c = (Long)a + (Long) b;
			return c;
		}else if(a instanceof Integer){
			Integer c = (Integer)a + (Integer) b;
			return c;
		}else if(a instanceof Short){
			Integer c = (Short)a + (Short) b;
			return c;
		}
		return null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int compare(Number a, Number b){
		if(a==null){
			return -1;
		}
		if(b==null){
			return 1;
		}
		if(a instanceof Comparable){
			return ((Comparable)a).compareTo((Comparable)b);
		}
		return 0;
	}

	public static Number except(Number number, int except){
		if(except==0){
			return 0;
		}
		if(number instanceof Double){
			Double c = (Double)number / except;
			return c;
		}else if(number instanceof Float){
			Float c = (Float)number / except;
			return c;
		}else if(number instanceof Long){
			Long c = (Long)number / except;
			return c;
		}else if(number instanceof Integer){
			Integer c = (Integer)number / except;
			return c;
		}else if(number instanceof Short){
			Integer c = (Short)number / except;
			return c;
		}
		return null;
	}
}

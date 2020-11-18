package com.jimistore.boot.nemo.security.helper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtil {

	@SuppressWarnings("rawtypes")
	public static Map hasMap(Object... objects) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (objects.length % 2 == 1) {
			throw new RuntimeException("key和value的数量不一致");
		}
		for (int i = 0; i < objects.length / 2; i++) {
			map.put(objects[i * 2].toString(), objects[i * 2 + 1]);
		}
		return map;

	}
	
	public static Map<Object,Object> hasLinkedMap(Object... objects){
		Map<Object,Object> map = new LinkedHashMap<Object,Object>();
		if(objects.length%2==1){
			throw new RuntimeException("key和value的数量不一致");
		}
		for(int i=0;i<objects.length/2;i++){
			map.put(objects[i*2].toString(), objects[i*2+1]);
		}
		return map;
		
	}
}

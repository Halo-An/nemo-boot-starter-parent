package com.jimistore.boot.nemo.sliding.window.helper;

import java.util.ArrayList;
import java.util.List;

import com.jimistore.boot.nemo.sliding.window.core.INoticeEvent;
import com.jimistore.boot.nemo.sliding.window.core.INoticeStatisticsEvent;
import com.jimistore.boot.nemo.sliding.window.core.NoticeEvent;
import com.jimistore.boot.nemo.sliding.window.core.NoticeStatisticsEvent;

public class NoticeEventHelper {
	
	/**
	 * 合并窗口运算
	 * @param event
	 * @param length
	 * @return
	 */
	public static <T extends Number> INoticeEvent<T> mergeWindow(INoticeEvent<T> event, Integer length){
		NoticeEvent<T> noticeEvent = (NoticeEvent<T>) event;
		noticeEvent.setValue(NoticeEventHelper.mergeWindow(event.getValue(), length));
		return noticeEvent;
	}
	
	/**
	 * 合并窗口运算
	 * @param event
	 * @param length
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> List<T> mergeWindow(List<T> valueList, Integer length){
		if(valueList.size()<length){
			throw new RuntimeException("window's length must be less than capacity");
		}
		int capacity = valueList.size()-length+1;
		List<T> value = new ArrayList<T>(capacity);
		for(int i=0;i<capacity;i++){
			Number t = 0;
			for(int j=0;j<length;j++){
				t = NumberUtil.add(t, valueList.get(i+j));
			}
			value.add((T)t);
		}
		return value;
	}
	
	/**
	 * 计算最大最小平均累计瞬时值
	 * @param event
	 * @return
	 */
	public static <T extends Number> INoticeStatisticsEvent<T> count(INoticeEvent<T> event){
		return new NoticeStatisticsEvent<T>(event);
	}

}

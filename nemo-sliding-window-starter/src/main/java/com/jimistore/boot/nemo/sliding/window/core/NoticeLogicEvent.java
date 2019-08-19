package com.jimistore.boot.nemo.sliding.window.core;

import java.util.List;
import java.util.Map;

public class NoticeLogicEvent<T extends Number> extends NoticeStatisticsEvent<T> implements INoticeEvent<T> {

	List<Map<String, T>> originMap;

	public NoticeLogicEvent(INoticeEvent<T> noticeEvent) {
		super(noticeEvent);
	}

	public List<Map<String, T>> getOriginMap() {
		return originMap;
	}

	public NoticeLogicEvent<T> setOriginMap(List<Map<String, T>> originMap) {
		this.originMap = originMap;
		return this;
	}

}

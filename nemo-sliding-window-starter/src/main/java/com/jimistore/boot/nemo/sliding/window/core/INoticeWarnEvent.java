package com.jimistore.boot.nemo.sliding.window.core;

public interface INoticeWarnEvent<T extends Number> extends INoticeStatisticsEvent<T> {
	
	public boolean isWarn();

}

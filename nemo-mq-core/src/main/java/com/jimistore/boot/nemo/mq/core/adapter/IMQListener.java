package com.jimistore.boot.nemo.mq.core.adapter;

/**
 * 定义监听器接口
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public interface IMQListener {
	
	/**
	 * 监听接收的信息
	 * @param mQReceiver
	 * @param mQName
	 */
	public void listener(IMQReceiver mQReceiver);

}

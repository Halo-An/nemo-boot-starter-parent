package com.jimistore.boot.nemo.mq.core.adapter;

import com.jimistore.boot.nemo.mq.core.enums.QueueType;

/**
 * 定义发送端接口
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public interface IMQSender {
	
	/**
	 * 向队列发送消息
	 * @param dataSource 数据源
	 * @param mqname 队列名称
	 * @param type 发送类型
	 * @param msg 消息
	 */
	public void send(String dataSource, String mqname, QueueType type, Object msg);

}

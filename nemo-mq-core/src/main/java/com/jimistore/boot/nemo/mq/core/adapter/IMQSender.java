package com.jimistore.boot.nemo.mq.core.adapter;

/**
 * 定义发送端接口
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public interface IMQSender {
	
	/**
	 * 向队列发送消息
	 * @param msg 消息
	 */
	public void send(MQMessage msg);

}

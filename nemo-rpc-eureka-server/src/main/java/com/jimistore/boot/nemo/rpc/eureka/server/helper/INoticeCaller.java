package com.jimistore.boot.nemo.rpc.eureka.server.helper;

import com.jimistore.boot.nemo.rpc.eureka.server.request.NoticeRequest;

public interface INoticeCaller {

	/**
	 * 发送通知
	 * 
	 * @param notice
	 */
	public void notice(NoticeRequest notice);

}

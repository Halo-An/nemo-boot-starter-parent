package com.jimistore.boot.nemo.rpc.eureka.server.request;

public class DingDingNoticeRequest extends NoticeRequest {

	String title;

	String robot;

	public String getTitle() {
		return title;
	}

	public DingDingNoticeRequest setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getRobot() {
		return robot;
	}

	public DingDingNoticeRequest setRobot(String robot) {
		this.robot = robot;
		return this;
	}

}

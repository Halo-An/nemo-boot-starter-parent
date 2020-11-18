package com.jimistore.boot.nemo.rpc.eureka.server.request;

public class NoticeRequest {

	String content;

	String[] tos;

	public String getContent() {
		return content;
	}

	public NoticeRequest setContent(String content) {
		this.content = content;
		return this;
	}

	public String[] getTos() {
		return tos;
	}

	public NoticeRequest setTos(String[] tos) {
		this.tos = tos;
		return this;
	}

}

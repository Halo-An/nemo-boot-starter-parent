package com.jimistore.boot.nemo.monitor.server.helper;

public class DingDingMsg {
	
	String msgtype;
	
	Text text;
	
	At at;

	public String getMsgtype() {
		return msgtype;
	}

	public DingDingMsg setMsgtype(String msgtype) {
		this.msgtype = msgtype;
		return this;
	}

	public Text getText() {
		return text;
	}

	public DingDingMsg setText(Text text) {
		this.text = text;
		return this;
	}

	public At getAt() {
		return at;
	}

	public DingDingMsg setAt(At at) {
		this.at = at;
		return this;
	}

}

class Text{
	
	String content;

	public String getContent() {
		return content;
	}

	public Text setContent(String content) {
		this.content = content;
		return this;
	}
	
	
}
class At{
	
	String[] atMobiles;
	
	boolean isAtAll;

	public String[] getAtMobiles() {
		return atMobiles;
	}

	public At setAtMobiles(String[] atMobiles) {
		this.atMobiles = atMobiles;
		return this;
	}

	public boolean isAtAll() {
		return isAtAll;
	}

	public At setAtAll(boolean isAtAll) {
		this.isAtAll = isAtAll;
		return this;
	}
	
}

package com.jimistore.boot.nemo.monitor.server.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DingDingCaller {

	private static final Logger log = LoggerFactory.getLogger(DingDingCaller.class);

	@Autowired
	private RestTemplate restTemplate;

	public void sendRobotNotice(String title, String msg, String toRobot, String... toPhones) {
		if (log.isDebugEnabled()) {
			log.debug(
					String.format("call sendRobotNotice of dingding, the toRobot is %s, the msg is %s", toRobot, msg));
		}
		DingDingMsg dingDingMsg = new DingDingMsg().setMsgtype(DingDingMsg.MSG_TYPE_MARKDOWN)
				.setMarkdown(new Markdown().setTitle(title).setText(msg));

		if (toPhones == null) {
			dingDingMsg.setAt(new At().setAtAll(true));
		} else {
			dingDingMsg.setAt(new At().setAtAll(false).setAtMobiles(toPhones));
		}
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", "application/json;charset=utf-8");

		HttpEntity<DingDingMsg> requestEntity = new HttpEntity<DingDingMsg>(dingDingMsg, requestHeaders);

		ResponseEntity<String> response = restTemplate.exchange(toRobot, HttpMethod.POST, requestEntity, String.class);
		String result = response.getBody();
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"call sendRobotNotice of dingding, the toRobot is %s, the msg is %s, the result is %s", toRobot,
					msg, result));
		}

	}
}

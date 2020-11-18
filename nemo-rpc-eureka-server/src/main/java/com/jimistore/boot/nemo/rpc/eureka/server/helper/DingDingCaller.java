package com.jimistore.boot.nemo.rpc.eureka.server.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingMsgRequest;
import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingMsgRequest.At;
import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingMsgRequest.Markdown;
import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingNoticeRequest;
import com.jimistore.boot.nemo.rpc.eureka.server.request.NoticeRequest;

@Component
public class DingDingCaller implements INoticeCaller {

	private static final Logger log = LoggerFactory.getLogger(DingDingCaller.class);

	@Autowired
	private RestTemplate restTemplate;

	private void sendRobotNotice(String title, String msg, String toRobot, String... toPhones) {
		if (log.isDebugEnabled()) {
			log.debug(
					String.format("call sendRobotNotice of dingding, the toRobot is %s, the msg is %s", toRobot, msg));
		}
		DingDingMsgRequest dingDingMsg = new DingDingMsgRequest().setMsgtype(DingDingMsgRequest.MSG_TYPE_MARKDOWN)
				.setMarkdown(new Markdown().setTitle(title).setText(msg));

		if (toPhones == null) {
			dingDingMsg.setAt(new At().setAtAll(true));
		} else {
			dingDingMsg.setAt(new At().setAtAll(false).setAtMobiles(toPhones));
			StringBuilder sb = new StringBuilder(dingDingMsg.getMarkdown().getText());
			sb.append("\n");
			for (String phone : toPhones) {
				sb.append("@").append(phone).append(" ");
			}
			dingDingMsg.getMarkdown().setText(sb.toString());
		}
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Content-Type", "application/json;charset=utf-8");

		HttpEntity<DingDingMsgRequest> requestEntity = new HttpEntity<DingDingMsgRequest>(dingDingMsg, requestHeaders);

		ResponseEntity<String> response = restTemplate.exchange(toRobot, HttpMethod.POST, requestEntity, String.class);
		String result = response.getBody();
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"call sendRobotNotice of dingding, the toRobot is %s, the msg is %s, the result is %s", toRobot,
					msg, result));
		}

	}

	@Override
	public void notice(NoticeRequest request) {
		DingDingNoticeRequest notice = DingDingNoticeRequest.class.cast(request);
		this.sendRobotNotice(notice.getTitle(), notice.getContent(), notice.getRobot(), notice.getTos());
	}
}

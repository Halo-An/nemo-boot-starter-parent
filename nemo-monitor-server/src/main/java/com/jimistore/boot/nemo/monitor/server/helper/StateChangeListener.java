package com.jimistore.boot.nemo.monitor.server.helper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.event.ClientApplicationStatusChangedEvent;
import de.codecentric.boot.admin.notify.AbstractStatusChangeNotifier;

@Component
public class StateChangeListener extends AbstractStatusChangeNotifier {

	private static final Logger log = Logger.getLogger(StateChangeListener.class);

	@Autowired
	DingDingCaller caller;

	@Autowired
	IWhiteStorage whiteStorage;

	@Value("${alerm.dingding.enabled:false}")
	boolean enabled;

	@Value("${alerm.dingding.to-robot:}")
	String toRobot;

	@Value("${alerm.dingding.to-phone:18667163872}")
	String[] toPhones;

	@Value("${alerm.dingding.msg:}")
	String msg;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");

	private final SpelExpressionParser parser = new SpelExpressionParser();

	public StateChangeListener setCaller(DingDingCaller caller) {
		this.caller = caller;
		return this;
	}

	public StateChangeListener setToRobot(String toRobot) {
		this.toRobot = toRobot;
		return this;
	}

	public StateChangeListener setToPhones(String[] toPhones) {
		this.toPhones = toPhones;
		return this;
	}

	public StateChangeListener setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		super.setEnabled(enabled);
	}

	@Override
	protected void doNotify(ClientApplicationEvent event) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug(String.format("application status has changed, application name is %s, application info is %s",
					event.getApplication().getName(), event.getApplication().getStatusInfo().getDetails()));
		}

		// 校验白名单
		if (this.matchWhiteList(event)) {
			return;
		}

		// 拼装推送内容
		String message = this.parseMsg(event);

		// 发送通知
		if (enabled) {
			caller.sendRobotNotice(message, toRobot, toPhones);
		}
	}

	/**
	 * 拼装推送消息内容
	 * 
	 * @param event
	 * @return
	 */
	private String parseMsg(ClientApplicationEvent event) {
		if (StringUtils.isEmpty(msg)) {
			msg = " #{application.name} (#{application.id}) status changed from #{from.status} to #{to.status} #{application.healthUrl}";
		}
		StandardEvaluationContext context = new StandardEvaluationContext(event);
		String message = parser.parseExpression(msg, ParserContext.TEMPLATE_EXPRESSION).getValue(context, String.class);
		StringBuilder detailMsg = new StringBuilder();

		ClientApplicationStatusChangedEvent e = (ClientApplicationStatusChangedEvent) event;
		if (!e.getTo().getStatus().equals("UP")) {
			for (Entry<String, Serializable> entry : e.getTo().getDetails().entrySet()) {
				if (entry.getValue() instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, Object> data = (Map<String, Object>) entry.getValue();
					if (!data.get("status").equals("UP")) {
						detailMsg.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
					}
				}
			}
		}
		if (detailMsg.length() > 0) {
			message = String.format("%s \n \n %s", message, detailMsg.toString());
		}
		Calendar time = Calendar.getInstance();
		time.setTimeInMillis(event.getTimestamp());
		message = String.format("%s \n %s", sdf.format(time.getTime()), message);
		return message;
	}

	private boolean matchWhiteList(ClientApplicationEvent event) {
		if (event != null && event.getApplication() != null && event.getApplication().getName() != null) {
			String serviceName = event.getApplication().getName();
			Set<String> whiteSet = whiteStorage.getWhiteServiceSet();
			if (serviceName != null && whiteSet != null && whiteSet.size() > 0) {
				for (String white : whiteSet) {
					if (white != null && serviceName.toUpperCase().indexOf(white.toUpperCase()) >= 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

}

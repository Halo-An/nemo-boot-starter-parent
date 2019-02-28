package com.jimistore.boot.nemo.monitor.server.helper;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

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

	@Value("${alerm.dingding.enabled:false}")
	boolean enabled;

	@Value("${alerm.dingding.to-robot:}")
	String toRobot;

	@Value("${alerm.dingding.to-phone:18667163872}")
	String[] toPhones;

	@Value("${alerm.dingding.msg:}")
	String msg;

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
			log.debug(String.format("application status has changed, application info is %s",
					event.getApplication().getStatusInfo().getDetails()));
		}
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
		if (enabled) {
			caller.sendRobotNotice(message, toRobot, toPhones);
		}
	}

}

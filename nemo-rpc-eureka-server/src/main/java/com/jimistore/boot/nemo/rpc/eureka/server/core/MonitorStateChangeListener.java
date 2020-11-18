package com.jimistore.boot.nemo.rpc.eureka.server.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.rpc.eureka.server.dao.IServiceConfigStorage;
import com.jimistore.boot.nemo.rpc.eureka.server.entity.ServiceConfig;
import com.jimistore.boot.nemo.rpc.eureka.server.helper.DingDingCaller;
import com.jimistore.boot.nemo.rpc.eureka.server.helper.INoticeCaller;
import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingNoticeRequest;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.events.InstanceStatusChangedEvent;
import de.codecentric.boot.admin.server.domain.values.StatusInfo;
import de.codecentric.boot.admin.server.notify.AbstractStatusChangeNotifier;
import reactor.core.publisher.Mono;

@Component
public class MonitorStateChangeListener extends AbstractStatusChangeNotifier implements InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(MonitorStateChangeListener.class);

	@Autowired
	INoticeCaller caller;

	@Autowired
	IServiceConfigStorage serviceConfigStorage;

	private final SpelExpressionParser parser = new SpelExpressionParser();

	private final ParserContext parserContext = new TemplateParserContext("{", "}");

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${notify.monitor.enabled:true}")
	boolean enabled;

	@Value("${notify.monitor.msg-temp-path:notify-template.md}")
	String msgTempPath;

	String msgTemp;

	public MonitorStateChangeListener(InstanceRepository repository) {
		super(repository);
	}

	@Override
	protected Mono<Void> doNotify(InstanceEvent event, Instance instance) {
		// 服务状态改变事件
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("request doNotify, event is %s, instance is %s", event, instance));
		}
		if (!enabled) {
			return null;
		}

		return Mono.fromRunnable(() -> {
			// 如果发生异常,则最大重试3次
			for (int i = 0; i < 3; i++) {
				ServiceConfig service = serviceConfigStorage.get(instance.getRegistration().getName());
				if (service != null && !service.isDisabled()) {
					if (event instanceof InstanceStatusChangedEvent) {
						String content = this.parseTemp(this.filter(event), instance);
						caller.notice(new DingDingNoticeRequest().setTitle("监控到异常报警")
								.setRobot(service.getRobot())
								.setTos(service.getPhones())
								.setContent(content));
						return;
					}
				}
			}
		});
	}

	/**
	 * 解析通知模板
	 * 
	 * @param event
	 * @param instance
	 * @return
	 */
	private String parseTemp(InstanceEvent event, Instance instance) {

		StandardEvaluationContext context = new StandardEvaluationContext(event);
		context.setVariable("event", event);
		context.setVariable("instance", instance);
		context.setVariable("objectMapper", objectMapper);
		return parser.parseExpression(msgTemp, parserContext).getValue(context, String.class);
	}

	/**
	 * 过滤不需要的数据
	 * 
	 * @param event
	 * @return
	 */
	private InstanceEvent filter(InstanceEvent event) {

		if (event instanceof InstanceStatusChangedEvent) {
			Map<String, Object> details = new HashMap<>();
			InstanceStatusChangedEvent se = InstanceStatusChangedEvent.class.cast(event);
			Iterator<Entry<String, Object>> it = se.getStatusInfo().getDetails().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object> entry = it.next();
				Object value = entry.getValue();
				if (value instanceof Map) {
					Map<?, ?> map = Map.class.cast(value);
					Object status = map.get("status");
					if (status == null || "UP".equals(status)) {
						continue;
					}
				}
				details.put(entry.getKey(), entry.getValue());
			}
			StatusInfo statusInfo = StatusInfo.valueOf(se.getStatusInfo().getStatus(), details);
			return new InstanceStatusChangedEvent(se.getInstance(), se.getVersion(), statusInfo);
		}
		return event;
	}

	private String readFileContent(String fileName) {
		BufferedReader reader = null;
		StringBuffer sbf = new StringBuffer();
		try {
			ClassPathResource classPathResource = new ClassPathResource(fileName);
			File file = classPathResource.getFile();
			reader = new BufferedReader(new FileReader(file));
			String tempStr;
			while ((tempStr = reader.readLine()) != null) {
				sbf.append(tempStr).append("\n");
			}
			reader.close();
			return sbf.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public MonitorStateChangeListener setCaller(DingDingCaller caller) {
		this.caller = caller;
		return this;
	}

	public MonitorStateChangeListener setServiceConfigStorage(IServiceConfigStorage serviceConfigStorage) {
		this.serviceConfigStorage = serviceConfigStorage;
		return this;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		msgTemp = this.readFileContent(msgTempPath);
	}

}

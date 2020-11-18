package com.jimistore.boot.nemo.rpc.eureka.server.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.jimistore.boot.nemo.rpc.eureka.server.dao.IServiceConfigStorage;
import com.jimistore.boot.nemo.rpc.eureka.server.entity.ServiceConfig;
import com.jimistore.boot.nemo.rpc.eureka.server.helper.DingDingCaller;
import com.jimistore.boot.nemo.rpc.eureka.server.helper.INoticeCaller;
import com.jimistore.boot.nemo.rpc.eureka.server.request.DingDingNoticeRequest;

@Component
public class EurekaStateChangeListener {

	@Autowired
	INoticeCaller caller;

	@Autowired
	IServiceConfigStorage serviceConfigStorage;

	@Value("${notify.eureka.enabled:false}")
	boolean enabled;

	@EventListener
	public void listener(EurekaServerStartedEvent event) {
		if (!enabled) {
			return;
		}
		// 服务端啟動事件
		ServiceConfig service = serviceConfigStorage.get(IServiceConfigStorage.SERVICE_DEFAULT_KEY);
		if (service != null && !service.isDisabled()) {
			String title = "监控到异常报警";
			String content = "注册中心已启动";
			caller.notice(new DingDingNoticeRequest().setTitle(title)
					.setRobot(service.getRobot())
					.setTos(service.getPhones())
					.setContent(content));
		}
	}

	@EventListener
	public void listener(EurekaInstanceCanceledEvent event) {
		if (!enabled) {
			return;
		}
		// 服务断线事件
		String serviceName = event.getAppName();
		ServiceConfig service = serviceConfigStorage.get(serviceName);
		if (service != null && !service.isDisabled()) {
			String title = "监控到异常报警";
			String content = String.format("注册中心的服务[%s]已下线，请排查原因。", serviceName);
			caller.notice(new DingDingNoticeRequest().setTitle(title)
					.setRobot(service.getRobot())
					.setTos(service.getPhones())
					.setContent(content));
		}
	}

	@EventListener
	public void listener(EurekaInstanceRegisteredEvent event) {

	}

	@EventListener
	public void listener(EurekaInstanceRenewedEvent event) {
	}

	@EventListener
	public void listener(EurekaRegistryAvailableEvent event) {
	}

	public EurekaStateChangeListener setCaller(DingDingCaller caller) {
		this.caller = caller;
		return this;
	}

	public EurekaStateChangeListener setServiceConfigStorage(IServiceConfigStorage serviceConfigStorage) {
		this.serviceConfigStorage = serviceConfigStorage;
		return this;
	}

}

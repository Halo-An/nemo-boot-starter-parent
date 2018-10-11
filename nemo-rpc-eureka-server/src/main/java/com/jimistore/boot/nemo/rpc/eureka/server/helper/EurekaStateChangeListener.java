package com.jimistore.boot.nemo.rpc.eureka.server.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaRegistryAvailableEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EurekaStateChangeListener {
	
	@Autowired
	DingDingCaller caller;
	
	@Value("${alerm.dingding.enabled:false}")
	boolean enabled;
	
	@Value("${alerm.dingding.to-robot:}")
	String toRobot;

	@Value("${alerm.dingding.to-phone:18667163872}")
	String[] toPhones;

	@EventListener
	public void listener(EurekaInstanceCanceledEvent event) {
		if(!enabled){
			return ;
		}
		// 服务断线事件
		String serverId = event.getServerId();
		String msg = String.format("注册中心的服务[%s]已下线，请排查原因。", serverId);
		caller.sendRobotNotice(msg, toRobot, toPhones);
	}

	@EventListener
	public void listener(EurekaInstanceRegisteredEvent event) {
		if(!enabled){
			return ;
		}
		// 服务註冊事件
//		String ip = event.getInstanceInfo().getIPAddr();
//		int port = event.getInstanceInfo().getPort();
//		String appName = event.getInstanceInfo().getAppName();
//		String msg = String.format("服务[%s:%s:%s]已注册到注册中心。", ip, appName, port);
//		caller.sendRobotNotice(msg, toRobot, toPhones);
	}

	@EventListener
	public void listener(EurekaInstanceRenewedEvent event) {
		if(!enabled){
			return ;
		}
		// 服务重新註冊事件
//		String ip = event.getInstanceInfo().getIPAddr();
//		int port = event.getInstanceInfo().getPort();
//		String appName = event.getInstanceInfo().getAppName();
//		String msg = String.format("客户端[%s:%s]的服务[%s]已重新重新注册到注册中心", ip, port, appName);
//		caller.sendRobotNotice(msg, toRobot, toPhones);
	}

	@EventListener
	public void listener(EurekaRegistryAvailableEvent event) {
		if(!enabled){
			return ;
		}
		// 服务断线事件

	}

	@EventListener
	public void listener(EurekaServerStartedEvent event) {
		if(!enabled){
			return ;
		}
		// 服务端啟動事件
		caller.sendRobotNotice("注册中心已启动", toRobot, toPhones);
	}

	public EurekaStateChangeListener setCaller(DingDingCaller caller) {
		this.caller = caller;
		return this;
	}

	public EurekaStateChangeListener setToRobot(String toRobot) {
		this.toRobot = toRobot;
		return this;
	}

	public EurekaStateChangeListener setToPhones(String[] toPhones) {
		this.toPhones = toPhones;
		return this;
	}
	
	
}

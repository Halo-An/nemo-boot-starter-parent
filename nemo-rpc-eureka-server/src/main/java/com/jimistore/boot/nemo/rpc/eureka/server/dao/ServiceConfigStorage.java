package com.jimistore.boot.nemo.rpc.eureka.server.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jimistore.boot.nemo.rpc.eureka.server.entity.ServiceConfig;

/**
 * 服务加载数据库实现
 * 
 * @author chenqi
 * @date 2020年11月10日
 *
 */
@Component
public class ServiceConfigStorage extends Thread implements IServiceConfigStorage, InitializingBean {

	private static final String SYNC_THREAD_NAME = "nemo-monitor-service-config-sync";
	private static final Logger LOG = LoggerFactory.getLogger(ServiceConfigStorage.class);

	private Map<String, ServiceConfig> serviceConfigMap = new HashMap<>();
	private int syncInterval;
	private boolean start = true;

	@Autowired
	ServiceConfigDao dao;

	@Value("${nemo.monitor.sync-interval:300000}")
	public ServiceConfigStorage setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
		return this;
	}

	@Override
	public ServiceConfig get(String id) {
		ServiceConfig service = serviceConfigMap.get(id);
		if (service != null) {
			return service;
		}
		return serviceConfigMap.get(IServiceConfigStorage.SERVICE_DEFAULT_KEY);
	}

	public void sync() {
		long old = System.currentTimeMillis();
		Map<String, ServiceConfig> dataMap = new HashMap<>();
		List<ServiceConfig> dataList = dao.listAll();
		if (dataList != null) {
			for (ServiceConfig serviceConfig : dataList) {
				dataMap.put(serviceConfig.getId(), serviceConfig);
			}
			serviceConfigMap = dataMap;
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace(String.format("sync done, cost is %s ms", System.currentTimeMillis() - old));
		}
	}

	@Override
	public void run() {
		while (start) {
			try {
				this.sync();
				Thread.sleep(syncInterval);
			} catch (Exception e) {
				LOG.warn(e.getMessage(), e);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.setName(SYNC_THREAD_NAME);
		this.start();
	}

	@Override
	public void destroy() {
		start = false;
	}

}

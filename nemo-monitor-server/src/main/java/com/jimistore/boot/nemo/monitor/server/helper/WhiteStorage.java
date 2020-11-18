package com.jimistore.boot.nemo.monitor.server.helper;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class WhiteStorage extends Thread implements IWhiteStorage, InitializingBean, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(WhiteStorage.class);

	private int syncInterval;
	private static final String REDIS_WHILIE_KEY = "nemo-monitor-white-list";
	private static final String SYNC_THREAD_NAME = "nemo-monitor-white-list-sync";

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	Set<String> set = null;

	private boolean start = true;

	@Value("${nemo.monitor.sync-interval:300000}")
	public WhiteStorage setSyncInterval(int syncInterval) {
		this.syncInterval = syncInterval;
		return this;
	}

	@Override
	public Set<String> getWhiteServiceSet() {
		return set;
	}

	public void sync() {
		long old = System.currentTimeMillis();
		Set<String> temp = redisTemplate.opsForSet().members(REDIS_WHILIE_KEY);
		set = temp;
		if (log.isTraceEnabled()) {
			log.trace(String.format("sync done, cost is %s ms", System.currentTimeMillis() - old));
		}
	}

	@Override
	public void run() {
		while (start) {
			try {
				this.sync();
				Thread.sleep(syncInterval);
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
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

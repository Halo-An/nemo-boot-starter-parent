package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class MutilDataSourceHealthEndPoint implements HealthIndicator {

	private static final Logger log = LoggerFactory.getLogger(MutilDataSourceHealthEndPoint.class);

	List<BaseSessionFactory> sessionFactoryList;

	public MutilDataSourceHealthEndPoint setSessionFactoryList(List<BaseSessionFactory> sessionFactoryList) {
		this.sessionFactoryList = sessionFactoryList;
		return this;
	}

	@Override
	public Health health() {
		if (sessionFactoryList == null || sessionFactoryList.size() == 0) {
			return new Health.Builder().down().build();
		}

		boolean health = true;
		Health.Builder builder = new Health.Builder();
		for (BaseSessionFactory baseSessionFactory : sessionFactoryList) {
			ComboPooledDataSource dataSource = (ComboPooledDataSource) baseSessionFactory.getDataSource();
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("connections-max-num", dataSource.getMaxPoolSize());
				map.put("connections-total-num", dataSource.getNumConnections());
				map.put("connections-busy-num", dataSource.getNumBusyConnections());
				map.put("connections-idle-num", dataSource.getNumIdleConnections());
				builder.withDetail(baseSessionFactory.getKey(), map);
				if (dataSource.getNumBusyConnections() >= dataSource.getNumConnections()
						&& dataSource.getNumConnections() >= dataSource.getMaxPoolSize()) {
					health = false;
				}

			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				health = false;
			}
		}
		if (!health) {
			return builder.down().build();
		}
		return builder.up().build();
	}

}

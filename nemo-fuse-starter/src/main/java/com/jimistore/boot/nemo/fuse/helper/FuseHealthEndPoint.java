package com.jimistore.boot.nemo.fuse.helper;

import org.apache.log4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.jimistore.boot.nemo.fuse.core.FuseTemplate;
import com.jimistore.boot.nemo.fuse.core.IFuseInfo;

public class FuseHealthEndPoint implements HealthIndicator {

	private static final Logger log = Logger.getLogger(FuseHealthEndPoint.class);

	FuseTemplate fuseTemplate;

	public FuseHealthEndPoint setFuseTemplate(FuseTemplate fuseTemplate) {
		this.fuseTemplate = fuseTemplate;
		return this;
	}

	@Override
	public Health health() {

		boolean health = true;
		Health.Builder builder = new Health.Builder();
		for (IFuseInfo fuseInfo : fuseTemplate.getFuseInfoList()) {
			builder.withDetail(fuseInfo.getKey(), fuseInfo.getFuseState().name());
			if (!fuseInfo.getFuseState().isAvailable()) {
				health = false;
			}
		}
		if (!health) {
			log.warn("fuse has down");
			return builder.down().build();
		}
		return builder.up().build();
	}

}

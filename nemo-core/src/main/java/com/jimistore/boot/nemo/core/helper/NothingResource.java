package com.jimistore.boot.nemo.core.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimistore.boot.nemo.core.api.service.Resource;

public class NothingResource implements Resource {

	private static final Logger LOG = LoggerFactory.getLogger(NothingResource.class);

	@Override
	public void offline() {
		LOG.info("application will be offline");
	}

	@Override
	public void online() {
		LOG.info("application will be online");
	}

}

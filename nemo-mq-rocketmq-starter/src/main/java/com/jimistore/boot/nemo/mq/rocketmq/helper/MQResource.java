package com.jimistore.boot.nemo.mq.rocketmq.helper;

import java.util.Set;

import com.jimistore.boot.nemo.core.api.service.Resource;
import com.jimistore.boot.nemo.mq.rocketmq.adapter.RocketAdapter;

public class MQResource implements Resource {

	Set<RocketAdapter> rocketAdapterSet;

	public MQResource setRocketAdapterSet(Set<RocketAdapter> rocketAdapterSet) {
		this.rocketAdapterSet = rocketAdapterSet;
		return this;
	}

	@Override
	public void offline() {
		if (rocketAdapterSet != null && rocketAdapterSet.size() > 0) {
			for (RocketAdapter rocketAdapter : rocketAdapterSet) {
				rocketAdapter.shutdown();
			}
		}
	}

	@Override
	public void online() {
		if (rocketAdapterSet != null && rocketAdapterSet.size() > 0) {
			for (RocketAdapter rocketAdapter : rocketAdapterSet) {
				rocketAdapter.start();
			}
		}
	}

}

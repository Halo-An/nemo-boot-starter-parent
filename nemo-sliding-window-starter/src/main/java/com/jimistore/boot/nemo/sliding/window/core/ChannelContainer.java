package com.jimistore.boot.nemo.sliding.window.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;

public class ChannelContainer implements IChannelContainer {
	
	Map<String, Set<IChannel>> channelMap = new HashMap<String, Set<IChannel>>();
	
	Set<ISubscriber> subscriberSet = new HashSet<ISubscriber>();
	
	AntPathMatcher matcher = new AntPathMatcher();
	
	private static final Logger log = Logger.getLogger(ChannelContainer.class);

	@Override
	public ChannelContainer put(ISubscriber subscriber) {
		if(!subscriberSet.contains(subscriber)){
			subscriberSet.add(subscriber);
			for(String key:channelMap.keySet()){
				if(!this.match(key, subscriber.getTopicMatch())){
					continue;
				}
				
				if(channelMap.containsKey(key)){
					channelMap.put(key, new ConcurrentSkipListSet<IChannel>());
				}
				
				Set<IChannel> channelSet = channelMap.get(key);
				boolean flag = true;
				for(IChannel channel:channelSet){
					if(channel.getSubscriber().equals(subscriber)){
						flag=false;
					}
				}
				if(flag){
					
					channelSet.add(new Channel()
							.setSubscriber(subscriber)
							.setTopicKey(key)
							.setNextTime(System.currentTimeMillis()));
				}
			}
		}
		return this;
	}

	@Override
	public IChannelContainer put(String key) {
		if(channelMap.containsKey(key)){
			return this;
		}
		Set<IChannel> channelSet = new ConcurrentSkipListSet<IChannel>();
		channelMap.put(key, channelSet);
		for(ISubscriber subscriber:subscriberSet){
			if(log.isDebugEnabled()){
				log.debug(String.format("match subscriber %s:%s", subscriber.getTopicMatch(), key));
			}
			if(this.match(key, subscriber.getTopicMatch())){
				channelSet.add(new Channel()
						.setSubscriber(subscriber)
						.setTopicKey(key)
						.setNextTime(System.currentTimeMillis()));
				if(log.isDebugEnabled()){
					log.debug(String.format("create channel %s:%s ,channelSet:%s", subscriber.getTopicMatch(), key, channelSet));
				}
			}
		}
		
		return this;
	}

	@Override
	public Set<IChannel> match(String key) {
		return channelMap.get(key);
	}
	
	protected boolean match(String key, String topicMatch){
		return matcher.match(topicMatch, key);
	}

}

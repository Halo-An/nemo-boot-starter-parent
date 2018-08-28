package com.jimistore.boot.nemo.sliding.window.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;

public class ChannelContainer implements IChannelContainer {
	
	Map<String, List<IChannel>> channelMap = new HashMap<String, List<IChannel>>();
	
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
					channelMap.put(key, new ArrayList<IChannel>());
				}
				
				List<IChannel> channelSet = channelMap.get(key);
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
							.setNextTime(this.getNextTime(subscriber)));
				}
			}
		}
		return this;
	}

	@Override
	public IChannelContainer put(String key) {
		synchronized (channelMap) {
			if(channelMap.containsKey(key)){
				return this;
			}
			List<IChannel> channelSet = new ArrayList<IChannel>();
			channelMap.put(key, channelSet);
			for(ISubscriber subscriber:subscriberSet){
				if(log.isDebugEnabled()){
					log.debug(String.format("match subscriber %s:%s", subscriber.getTopicMatch(), key));
				}
				if(this.match(key, subscriber.getTopicMatch())){
					Channel channel = new Channel()
					.setSubscriber(subscriber)
					.setTopicKey(key)
					.setNextTime(this.getNextTime(subscriber));
					channelSet.add(channel);
					
					if(log.isDebugEnabled()){
						log.debug(String.format("create channel %s:%s , channelList:%s", subscriber.getTopicMatch(), key, channelSet));
					}
				}
			}
			
		}
		
		return this;
	}
	
	private long getNextTime(ISubscriber subscriber){
		long time = System.currentTimeMillis();
		long unitTime = subscriber.getTimeUnit().toMillis(1);
		
		return time + unitTime - time % unitTime;
	}

	@Override
	public List<IChannel> match(String key) {
		return channelMap.get(key);
	}
	
	protected boolean match(String key, String topicMatch){
		return matcher.match(topicMatch, key);
	}

	@Override
	public IChannelContainer delete(String key) {
		channelMap.remove(key);
		return this;
	}

}

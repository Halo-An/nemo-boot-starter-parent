package com.jimistore.boot.nemo.sliding.window.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;
import com.jimistore.boot.nemo.sliding.window.helper.PublishAspect;
import com.jimistore.boot.nemo.sliding.window.helper.PublisherHelper;
import com.jimistore.boot.nemo.sliding.window.helper.SlidingWindowClient;
import com.jimistore.boot.nemo.sliding.window.helper.SubscriberHelper;

@Configuration
@EnableConfigurationProperties(SlidingWindowProperties.class)
public class NemoSlidingWindowConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(SlidingWindowTemplate.class)
	public SlidingWindowTemplate slidingWindowTemplate(SlidingWindowProperties slidingWindowProperties){
		return SlidingWindowTemplate.create(slidingWindowProperties);
	}
	
	@Bean
	@ConditionalOnMissingBean(PublisherHelper.class)
	public PublisherHelper publisherHelper(SlidingWindowTemplate slidingWindowTemplate){
		return new PublisherHelper().setSlidingWindowTemplate(slidingWindowTemplate);
	}
	
	@Bean
	@ConditionalOnMissingBean(SubscriberHelper.class)
	public SubscriberHelper subscriberHelper(SlidingWindowTemplate slidingWindowTemplate){
		return new SubscriberHelper().setSlidingWindowTemplate(slidingWindowTemplate);
	}
	
	@Bean
	@ConditionalOnMissingBean(PublishAspect.class)
	public PublishAspect publishAspect(PublisherHelper publisherHelper){
		return new PublishAspect().setPublisherHelper(publisherHelper);
	}
	
	@Bean
	@ConditionalOnMissingBean(SlidingWindowClient.class)
	public SlidingWindowClient slidingWindowClient(PublisherHelper publisherHelper, SubscriberHelper subscriberHelper){
		return new SlidingWindowClient().setPublisherHelper(publisherHelper).setSubscriberHelper(subscriberHelper);
	}

}

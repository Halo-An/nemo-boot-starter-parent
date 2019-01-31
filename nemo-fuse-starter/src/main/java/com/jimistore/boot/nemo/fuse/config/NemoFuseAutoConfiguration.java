package com.jimistore.boot.nemo.fuse.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jimistore.boot.nemo.fuse.core.DefaultFuseStrategy;
import com.jimistore.boot.nemo.fuse.core.DefaultFuseStrategyFactory;
import com.jimistore.boot.nemo.fuse.core.FuseTemplate;
import com.jimistore.boot.nemo.fuse.core.IFuseStrategy;
import com.jimistore.boot.nemo.fuse.core.IFuseStrategyFactory;
import com.jimistore.boot.nemo.fuse.helper.FuseExecutorAspect;
import com.jimistore.boot.nemo.sliding.window.config.SlidingWindowProperties;
import com.jimistore.boot.nemo.sliding.window.core.SlidingWindowTemplate;

@Configuration
@EnableConfigurationProperties(FuseProperties.class)
public class NemoFuseAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(IFuseStrategy.class)
	public IFuseStrategy fuseStrategy(FuseProperties fuseProperties) {
		SlidingWindowTemplate slidingWindowTemplate = SlidingWindowTemplate.create(new SlidingWindowProperties()
				.setCacheModel(SlidingWindowProperties.CACHE_MODEL_LOCAL));
		return new DefaultFuseStrategy()
				.setSlidingWindowTemplate(slidingWindowTemplate)
				.setFuseProperties(fuseProperties);
	}
	
	@Bean
	@ConditionalOnMissingBean(IFuseStrategyFactory.class)
	public IFuseStrategyFactory fuseStrategyFactory(List<IFuseStrategy> fuseStrategyList) {
		return new DefaultFuseStrategyFactory()
				.setFuseStrategyList(fuseStrategyList);
	}
	
	@Bean
	@ConditionalOnMissingBean(FuseTemplate.class)
	public FuseTemplate fuseTemplate(FuseProperties fuseProperties, IFuseStrategyFactory fuseStrategyFactory) {
		return FuseTemplate.create(fuseProperties, fuseStrategyFactory);
	}
	
	@Bean
	@ConditionalOnMissingBean(FuseExecutorAspect.class)
	public FuseExecutorAspect FuseExecutorAspect(FuseTemplate fuseTemplate) {
		return new FuseExecutorAspect().setFuseTemplate(fuseTemplate);
	}
	

}

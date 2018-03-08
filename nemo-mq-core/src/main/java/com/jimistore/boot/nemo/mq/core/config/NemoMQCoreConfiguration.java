package com.jimistore.boot.nemo.mq.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.helper.AsynExecuter;
import com.jimistore.boot.nemo.mq.core.helper.MQCoreClient;

@Configuration
@AutoConfigureAfter(NemoMQCoreConfiguration.class)
public class NemoMQCoreConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper ObjectMapper(){
		return new ObjectMapper();
	}
	
	@Bean
	@ConditionalOnMissingBean(AsynExecuter.class)
	public AsynExecuter AsynExecuter(){
		return new AsynExecuter().setCapacity(5);
	}
	
	
	@Bean
	@ConditionalOnMissingBean(MQCoreClient.class)
	@ConditionalOnBean({IMQListener.class, IMQListener.class, ObjectMapper.class, AsynExecuter.class})
	public MQCoreClient MQClient(IMQSender mQSender,IMQListener mQListener, ObjectMapper objectMapper, AsynExecuter asynExecuter){
		return new MQCoreClient().setmQListener(mQListener).setmQSender(mQSender).setObjectMapper(objectMapper).setAsynExecuter(asynExecuter);
	}

}
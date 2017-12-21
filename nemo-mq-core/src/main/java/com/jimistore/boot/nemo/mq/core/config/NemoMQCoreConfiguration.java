package com.jimistore.boot.nemo.mq.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.helper.MQReceiverClient;
import com.jimistore.boot.nemo.mq.core.helper.MQSenderClient;

@Configuration
@AutoConfigureAfter(NemoMQCoreConfiguration.class)
public class NemoMQCoreConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper ObjectMapper(){
		return new ObjectMapper();
	}

	
	@Bean
	@ConditionalOnBean({IMQListener.class, ObjectMapper.class})
	@ConditionalOnMissingBean(MQReceiverClient.class)
	public MQReceiverClient MQReceiverClient(IMQListener mQListener, ObjectMapper objectMapper){
		return new MQReceiverClient().setmQListener(mQListener).setObjectMapper(objectMapper);
	}
	
	@Bean
	@ConditionalOnBean({IMQSender.class, ObjectMapper.class})
	@ConditionalOnMissingBean(MQSenderClient.class)
	public MQSenderClient MQSenderClient(IMQSender mQSender, ObjectMapper objectMapper){
		return new MQSenderClient().setmQSender(mQSender).setObjectMapper(objectMapper);
	}

}

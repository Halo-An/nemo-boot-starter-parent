package com.jimistore.boot.nemo.mq.core.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQDataSource;
import com.jimistore.boot.nemo.mq.core.helper.AsynExecuter;
import com.jimistore.boot.nemo.mq.core.helper.MQCoreClient;
import com.jimistore.boot.nemo.mq.core.helper.MQDataSourceGroup;
import com.jimistore.boot.nemo.mq.core.helper.MQNameHelper;

@Configuration
public class NemoMQCoreConfiguration {
	
	@Bean
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper ObjectMapper(){
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		return om;
	}
	
	@Bean
	@ConditionalOnMissingBean(AsynExecuter.class)
	public AsynExecuter AsynExecuter(){
		return new AsynExecuter().setCapacity(5);
	}

	
	@Bean
	@ConditionalOnMissingBean(MQNameHelper.class)
	public MQNameHelper MQNameHelper(){
		return new MQNameHelper();
	}
	
	
	@Bean
	@ConditionalOnMissingBean(MQCoreClient.class)
	@ConditionalOnBean({IMQDataSource.class, ObjectMapper.class, AsynExecuter.class})
	public MQCoreClient MQClient(List<MQDataSourceGroup> mQDataSourceGroupList, ObjectMapper objectMapper, AsynExecuter asynExecuter, MQNameHelper mQNameHelper){
		List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
		for(MQDataSourceGroup mQDataSourceGroup:mQDataSourceGroupList){
			if(mQDataSourceGroup.getDataSourceList()!=null){
				mQDataSourceList.addAll(mQDataSourceGroup.getDataSourceList());
			}
		}
		return new MQCoreClient()
				.setmQNameHelper(mQNameHelper)
				.setmQDataSourceList(mQDataSourceList)
				.setObjectMapper(objectMapper)
				.setAsynExecuter(asynExecuter);
	}

}

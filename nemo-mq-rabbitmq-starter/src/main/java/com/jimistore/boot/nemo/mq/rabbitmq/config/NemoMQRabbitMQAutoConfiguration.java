package com.jimistore.boot.nemo.mq.rabbitmq.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.config.NemoMQCoreConfiguration;
import com.jimistore.boot.nemo.mq.rabbitmq.adapter.RabbitAdapter;
import com.jimistore.boot.nemo.mq.rabbitmq.adapter.RabbitDataSource;

@Configuration
@AutoConfigureBefore(NemoMQCoreConfiguration.class)
public class NemoMQRabbitMQAutoConfiguration implements EnvironmentAware {
	
	private Map<String,CachingConnectionFactory> mQPropertiesMap=new HashMap<String,CachingConnectionFactory>();
	
	@Override
	public void setEnvironment(Environment environment) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
				environment, "spring.rabbitmq.");
		String dsPrefixs = propertyResolver.getProperty("names");
		if(dsPrefixs==null){
			Map<String, Object> dsMap = propertyResolver.getSubProperties("");
			if(dsMap==null){
				return ;
			}
			mQPropertiesMap.put(RabbitDataSource.DEFAULT, this.parse(dsMap));
			return ;
		}

		
		String[] dsPrefixsArr = dsPrefixs.split(",");
		if(dsPrefixsArr==null){
			return ;
		}
		for (String dsPrefix : dsPrefixsArr) {
			Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
			if(dsMap==null){
				return ;
			}
			mQPropertiesMap.put(dsPrefix, this.parse(dsMap));

		}
	}
	
	private CachingConnectionFactory parse(Map<String,Object> map){
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setHost(String.valueOf(map.get("host")));
		cachingConnectionFactory.setUsername(String.valueOf(map.get("username")));
		cachingConnectionFactory.setPassword(String.valueOf(map.get("password")));
		if(map.get("port")!=null){
			cachingConnectionFactory.setPort(Integer.parseInt(map.get("port").toString()));
		}
		return cachingConnectionFactory;
	}
	
	@Bean
	public List<RabbitDataSource> MQDataSource(){
		List<RabbitDataSource> mQDataSourceList = new ArrayList<RabbitDataSource>();
		for(Entry<String,CachingConnectionFactory> entry:mQPropertiesMap.entrySet()){
			RabbitTemplate rabbitTemplate = new RabbitTemplate();
			rabbitTemplate.setConnectionFactory(entry.getValue());
			mQDataSourceList.add(new RabbitDataSource()
					.setKey(entry.getKey())
					.setType("rabbitmq")
					.setRabbitTemplate(rabbitTemplate));
		}
		return mQDataSourceList;
	}
	
	@Bean
	public List<RabbitAdmin> rabbitAdmin(){
		List<RabbitAdmin> rabbitAdminList = new ArrayList<RabbitAdmin>();
		for(Entry<String,CachingConnectionFactory> entry:mQPropertiesMap.entrySet()){
			rabbitAdminList.add(new RabbitAdmin(entry.getValue()));
		}
		return rabbitAdminList;
	}
	
	@Bean
	@ConditionalOnMissingBean({IMQListener.class, IMQSender.class})
	public RabbitAdapter RabbitAdapter(List<RabbitDataSource> rabbitDataSourceList){
		return new RabbitAdapter().setmQDataSourceList(rabbitDataSourceList);
	}

}

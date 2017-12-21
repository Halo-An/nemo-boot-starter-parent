package com.jimistore.boot.nemo.mq.activemq.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsMessagingTemplate;

import com.jimistore.boot.nemo.mq.activemq.adapter.JMSAdapter;
import com.jimistore.boot.nemo.mq.activemq.adapter.JMSDataSource;
import com.jimistore.boot.nemo.mq.core.adapter.IMQListener;
import com.jimistore.boot.nemo.mq.core.adapter.IMQSender;
import com.jimistore.boot.nemo.mq.core.config.NemoMQCoreConfiguration;

@Configuration
@AutoConfigureAfter(NemoMQCoreConfiguration.class)
public class NemoMQActiveMQAutoConfiguration implements EnvironmentAware {
	
	private Map<String,ActiveMQProperties> activeMQPropertiesMap=new HashMap<String,ActiveMQProperties>();
	
	@Bean
	public List<JMSDataSource> MQDataSource(){
		List<JMSDataSource> mQDataSourceList = new ArrayList<JMSDataSource>();
		for(Entry<String,ActiveMQProperties> entry:activeMQPropertiesMap.entrySet()){

			JmsMessagingTemplate jmsMessagingTemplate = new JmsMessagingTemplate();
			jmsMessagingTemplate.setConnectionFactory(
					new ActiveMQConnectionFactory(
							entry.getValue().getUser(),
							entry.getValue().getPassword(),
							entry.getValue().getBrokerUrl()));
			mQDataSourceList.add(new JMSDataSource()
					.setKey(entry.getKey())
					.setType("activemq")
					.setJmsMessagingTemplate(jmsMessagingTemplate)
					);
		}
		return mQDataSourceList;
	}
	
//	@Bean
//	@ConditionalOnMissingBean(JmsMessagingTemplate.class)
//	public JmsMessagingTemplate JmsMessagingTemplate(ConnectionFactory connectionFactory){
//		JmsMessagingTemplate jmsMessagingTemplate = new JmsMessagingTemplate();
//		jmsMessagingTemplate.setConnectionFactory(connectionFactory);
//		return jmsMessagingTemplate;
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean(JMSDataSource.class)
//	public JMSDataSource MQDataSource(JmsMessagingTemplate jmsMessagingTemplate){
//		return new JMSDataSource().setKey(JMSDataSource.DEFAULT).setType("vm").setJmsMessagingTemplate(jmsMessagingTemplate);
//	}
	
	@Bean
	@ConditionalOnMissingBean({IMQListener.class, IMQSender.class})
	public JMSAdapter MQAdapter(List<JMSDataSource> mQDataSourceList){
		JMSAdapter mQAdapter = new JMSAdapter();
		mQAdapter.setmQDataSourceList(mQDataSourceList);
		return mQAdapter;
	}

	@Override
	public void setEnvironment(Environment environment) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
				environment, "spring.activemq.");
		String dsPrefixs = propertyResolver.getProperty("names");
		if(dsPrefixs==null){
			Map<String, Object> dsMap = propertyResolver.getSubProperties("");
			if(dsMap==null){
				return ;
			}
			activeMQPropertiesMap.put(JMSDataSource.DEFAULT, this.parse(dsMap));
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
			activeMQPropertiesMap.put(dsPrefix, this.parse(dsMap));

		}
	}
	
	private ActiveMQProperties parse(Map<String,Object> map){
		ActiveMQProperties activeMQProperties = new ActiveMQProperties();
		activeMQProperties.setBrokerUrl(String.valueOf(map.get("broker-url")));
		activeMQProperties.setUser(String.valueOf(map.get("user")));
		activeMQProperties.setPassword(String.valueOf(map.get("password")));
		return activeMQProperties;
	}

}

package com.jimistore.boot.nemo.mq.rabbitmq.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.jimistore.boot.nemo.mq.core.adapter.IMQDataSource;
import com.jimistore.boot.nemo.mq.core.config.NemoMQCoreConfiguration;
import com.jimistore.boot.nemo.mq.core.helper.MQDataSource;
import com.jimistore.boot.nemo.mq.core.helper.MQDataSourceGroup;
import com.jimistore.boot.nemo.mq.rabbitmq.adapter.RabbitAdapter;
import com.jimistore.boot.nemo.mq.rabbitmq.adapter.RabbitProperties;
import com.jimistore.boot.nemo.mq.rabbitmq.helper.RabbitAdapterHelper;

@Configuration
@AutoConfigureBefore(NemoMQCoreConfiguration.class)
public class NemoMQRabbitMQAutoConfiguration implements EnvironmentAware {
	
	public static final String RABBITMQ = "rabbitmq";
	
	private List<RabbitProperties> rabbitPropertiesList=new ArrayList<RabbitProperties>();
	
	@Override
	public void setEnvironment(Environment environment) {
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(
				environment, "nemo.mq.");
		String dsPrefixs = propertyResolver.getProperty("names");
		if(dsPrefixs==null){
			throw new RuntimeException("can not find nemo.mq.names in application.properties");
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
			Object type = dsMap.get("type");
			if(type==null){
				throw new RuntimeException("can not find nemo.mq.*.type in application.properties");
			}
			if(!type.equals(RABBITMQ)){
				continue ;
			}
			rabbitPropertiesList.add(this.parse(dsMap).setKey(dsPrefix));

		}
	}
	
	private RabbitProperties parse(Map<String,Object> map){
		RabbitProperties rabbitProperties = new RabbitProperties();
		rabbitProperties.setType(String.valueOf(map.get("type")));
		rabbitProperties.setHost(String.valueOf(map.get("host")));
		rabbitProperties.setUsername(String.valueOf(map.get("user")));
		rabbitProperties.setPassword(String.valueOf(map.get("password")));
		if(map.get("port")!=null){
			rabbitProperties.setPort(Integer.parseInt(map.get("port").toString()));
		}
		return rabbitProperties;
	}
	
	@Bean
	public RabbitAdapterHelper rabbitAdapterHelper(){
		return new RabbitAdapterHelper();
	}
	
	@Bean
	public MQDataSourceGroup initRabbitmqDataSourceGroup(RabbitAdapterHelper rabbitAdapterHelper){
		List<IMQDataSource> mQDataSourceList = new ArrayList<IMQDataSource>();
		for(RabbitProperties rabbitProperties:rabbitPropertiesList){
			RabbitAdapter adapter = new RabbitAdapter().setRabbitAdapterHelper(rabbitAdapterHelper).setRabbitProperties(rabbitProperties);
			mQDataSourceList.add(new MQDataSource().setSender(adapter).setListener(adapter)
					.setType(rabbitProperties.getType())
					.setKey(rabbitProperties.getKey()));
		}
		return new MQDataSourceGroup().setType(RABBITMQ).setDataSourceList(mQDataSourceList);
	}
	
	@Bean
	public List<RabbitAdmin> rabbitAdmin(){
		List<RabbitAdmin> rabbitAdminList = new ArrayList<RabbitAdmin>();
		for(RabbitProperties rabbitProperties:rabbitPropertiesList){
			rabbitAdminList.add(new RabbitAdmin(rabbitProperties));
		}
		return rabbitAdminList;
	}

}

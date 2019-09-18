package com.jimistore.boot.nemo.dao.hibernate.helper;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import com.jimistore.boot.nemo.dao.api.config.NemoDataSourceProperties;
import com.jimistore.boot.nemo.dao.hibernate.config.HibernateProperties;
import com.jimistore.boot.nemo.dao.hibernate.config.MutilDataSourceProperties;

public class MutilSessionFactoryHelper implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private static final Logger log = Logger.getLogger(MutilSessionFactoryHelper.class);

	MutilSessionFactory mutilSessionFactory;

	DataSourceSelector dataSourceSelector;

	MutilDataSourceProperties mutilDataSourceProperties;

	ApplicationContext applicationContext;

	public MutilSessionFactoryHelper() {
	}

	public MutilSessionFactoryHelper setMutilSessionFactory(MutilSessionFactory mutilSessionFactory) {
		this.mutilSessionFactory = mutilSessionFactory;
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public MutilSessionFactoryHelper setDataSourceSelector(DataSourceSelector dataSourceSelector) {
		this.dataSourceSelector = dataSourceSelector;
		return this;
	}

	public MutilSessionFactoryHelper setMutilDataSourceProperties(MutilDataSourceProperties mutilDataSourceProperties) {
		this.mutilDataSourceProperties = mutilDataSourceProperties;
		return this;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.debug("initting sessionFactorys");
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		// Bean的实例工厂
		DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) context.getBeanFactory();
		this.postProcessBeanFactory(dbf);
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) beanFactory;

		Map<String, NemoDataSourceProperties> dataSourceMap = mutilDataSourceProperties.getDatasource();
		Map<String, HibernateProperties> hibernateMap = mutilDataSourceProperties.getHibernate();
		for (Entry<String, NemoDataSourceProperties> entry : dataSourceMap.entrySet()) {

			NemoDataSourceProperties dataSourceProperties = entry.getValue();
			HibernateProperties hibernateProperties = hibernateMap.get(entry.getKey());

			HibernateNamingStrategy hibernateNamingStrategy = new HibernateNamingStrategy();
			hibernateNamingStrategy.setHibernateProperties(hibernateProperties);
			MutilHibernateNamingStrategy.put(entry.getKey(), hibernateNamingStrategy);

			// 注册datasource
			String dataSourceName = String.format("nemo-data-source-%s", entry.getKey());
			BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
					.rootBeanDefinition(
							dataSourceSelector.getNemoDataSourceFactoryClass(dataSourceProperties.getType()))
					.addPropertyValue("nemoDataSourceProperties", dataSourceProperties);
			dlbf.registerBeanDefinition(dataSourceName, beanDefinitionBuilder.getBeanDefinition());

			// 注册sessionfactory
			String sessionFactoryName = String.format("nemo-mutil-session-factory-%s", entry.getKey());
			beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(BaseSessionFactory.class)
					.addPropertyValue("key", entry.getKey()).addPropertyReference("dataSource", dataSourceName)
					.addPropertyValue("namingStrategy", hibernateNamingStrategy)
					.addPropertyValue("hibernatePropertie", hibernateMap.get(entry.getKey()))
					.addPropertyValue("dataSourcePropertie", dataSourceProperties);
			dlbf.registerBeanDefinition(sessionFactoryName, beanDefinitionBuilder.getBeanDefinition());

		}

	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

}

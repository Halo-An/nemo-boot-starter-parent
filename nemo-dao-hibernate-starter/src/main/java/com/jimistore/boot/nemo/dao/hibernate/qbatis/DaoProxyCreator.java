package com.jimistore.boot.nemo.dao.hibernate.qbatis;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import com.jimistore.boot.nemo.dao.hibernate.annotation.Dao;
import com.jimistore.boot.nemo.dao.hibernate.annotation.EnableHibernate;

/**
 * 代理对象初始化并注入到spring
 * 
 * @author chenqi
 * @date 2020年9月3日
 *
 */
public class DaoProxyCreator implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(DaoProxyCreator.class);

	List<IDaoExecutor> daoExecutorList;

	ApplicationContext applicationContext;

	public DaoProxyCreator setDaoExecutorList(List<IDaoExecutor> daoExecutorList) {
		this.daoExecutorList = daoExecutorList;
		return this;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
		DefaultListableBeanFactory dlbf = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(applicationContext);

		// 读取需要扫描的包
		String[] scanPackages = null;
		for (String beanName : dlbf.getBeanDefinitionNames()) {
			EnableHibernate enableJsonMQ = dlbf.findAnnotationOnBean(beanName, EnableHibernate.class);
			if (enableJsonMQ != null) {
				scanPackages = enableJsonMQ.daoScanPackage();
			}
		}

		// 扫描包
		for (String scanPackage : scanPackages) {
			String resolvedPath = resolvePackageToScan(scanPackage);
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("Scanning '%s' for DAO interfaces.", resolvedPath));
			}
			try {
				for (Resource resource : applicationContext.getResources(resolvedPath)) {
					if (resource.isReadable()) {
						MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
						ClassMetadata classMetadata = metadataReader.getClassMetadata();
						AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
						String jsonRpcPathAnnotation = Dao.class.getName();
						if (annotationMetadata.isAnnotated(jsonRpcPathAnnotation)) {
							String className = classMetadata.getClassName();

							Class<?> daoInterface = Class.forName(className);
							BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
									.rootBeanDefinition(DaoProxyFactory.class)
									.addPropertyValue("daoExecutorList", daoExecutorList)
									.addPropertyValue("daoInterface", daoInterface);
							dlbf.registerBeanDefinition(className + "-daoProxy",
									beanDefinitionBuilder.getBeanDefinition());

						}
					}
				}

			} catch (Exception e) {
				throw new RuntimeException(String.format("Cannot scan package '%s' for classes.", resolvedPath), e);
			}
		}

	}

	/**
	 * Converts the scanPackage to something that the resource loader can handle.
	 */
	private String resolvePackageToScan(String scanPackage) {
		return ResourceUtils.CLASSPATH_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanPackage)
				+ "/**/*.class";
	}

}

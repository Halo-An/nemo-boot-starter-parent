package com.jimistore.boot.nemo.id.generator.helper;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jimistore.boot.nemo.id.generator.core.IDGenerator;

public class NemoIdentifyGenerator implements IdentifierGenerator, Configurable {

	private static final Logger LOG = LoggerFactory.getLogger(NemoIdentifyGenerator.class);

	private static IDGeneratorHelper generatorHelper;

	private long num = 0;
	private String redisKey;
	private String sequence;
	private String format;
	private int start;
	private int length;

	public static void setIDGeneratorHelper(IDGeneratorHelper generatorHelper) {
		NemoIdentifyGenerator.generatorHelper = generatorHelper;
	}

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		this.length = Integer.parseInt(params.getProperty("length", "6"));
		this.start = Integer.parseInt(params.getProperty("start", "1000"));
		this.redisKey = params.getProperty("redisKey");
		this.sequence = params.getProperty("sequence", "8907213456");
		this.format = params.getProperty("format", "%s");
	}

	@Override
	public synchronized Serializable generate(SharedSessionContractImplementor session, Object object)
			throws HibernateException {
		num++;
		if (num <= start) {
			num = num + start;
		}
		if (redisKey != null) {
			num = generatorHelper.generatorNum(redisKey, start);
		}
		String id = new IDGenerator().generator(sequence, length, num);
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("generator %sth id，id is %s", num - start, id));
		}

		return String.format(format, id);
	}
}

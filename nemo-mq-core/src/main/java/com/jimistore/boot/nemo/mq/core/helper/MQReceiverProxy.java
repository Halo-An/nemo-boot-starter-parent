package com.jimistore.boot.nemo.mq.core.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jimistore.boot.nemo.mq.core.adapter.IMQReceiver;
import com.jimistore.boot.nemo.mq.core.enums.QueueType;

/**
 * 每个mq接收端的代理
 * @author chenqi
 * @Date 2017年12月19日
 *
 */
public class MQReceiverProxy implements IMQReceiver {
	
	private String mQName;
	
	private String mQDataSource;
	
	private QueueType queueType;
	
	private Object target;
	
	private Class<?>[] msgClass;
	
	ObjectMapper objectMapper;

	@Override
	public void receive(Object msg) throws Throwable {
		String msgStr = (String) msg;
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, String.class); 
		List<String> sourceList = objectMapper.readValue(msgStr, javaType);
		Object[] dest = new Object[sourceList.size()];
		for(int i=0;i<sourceList.size();i++){
			dest[i] = objectMapper.readValue(sourceList.get(i), msgClass[i]);
		}
		Method method = MQNameHelper.getMethodByMQNameAndTarget(mQName, msgClass, target);
		try{
			method.invoke(target, dest);
		}catch(InvocationTargetException e){
			throw e.getTargetException();
		}
	}

	public MQReceiverProxy setmQName(String mQName) {
		this.mQName = mQName;
		return this;
	}

	public MQReceiverProxy setQueueType(QueueType queueType) {
		this.queueType = queueType;
		return this;
	}

	public MQReceiverProxy setTarget(Object target) {
		this.target = target;
		return this;
	}

	@Override
	public QueueType getQueueType() {
		return queueType;
	}

	@Override
	public String getmQName() {
		return mQName;
	}

	@Override
	public Class<?>[] getMsgClass() {
		return msgClass;
	}

	public MQReceiverProxy setMsgClass(Class<?>[] msgClass) {
		this.msgClass = msgClass;
		return this;
	}

	public MQReceiverProxy setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	public MQReceiverProxy setmQDataSource(String mQDataSource) {
		this.mQDataSource = mQDataSource;
		return this;
	}

	@Override
	public String getmQDataSource() {
		return mQDataSource;
	}
	
}

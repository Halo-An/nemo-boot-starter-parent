package com.jimistore.boot.nemo.rpc.eureka.helper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.googlecode.jsonrpc4j.JsonRpcServer;

/**
 * 服务端
 * 
 * @author chenqi
 *
 */
public class NemoJsonRpcServer extends JsonRpcServer {

	private static final Logger LOGGER = Logger.getLogger(NemoJsonRpcServer.class.getName());

	private ObjectMapper mapper;

	public NemoJsonRpcServer(ObjectMapper mapper, Object handler, Class<?> remoteInterface) {
		super(mapper, handler, remoteInterface);
		this.mapper = mapper;
	}

	protected JsonNode invoke(Object target, Method m, List<JsonNode> params)
			throws IOException, IllegalAccessException, InvocationTargetException {

		// debug log
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Invoking method: " + m.getName());
		}

		// convert the parameters
		Object[] convertedParams = new Object[params.size()];
		Type[] parameterTypes = m.getGenericParameterTypes();

		for (int i = 0; i < parameterTypes.length; i++) {
			JsonParser paramJsonParser = mapper.treeAsTokens(params.get(i));
			JavaType paramJavaType = TypeFactory.defaultInstance().constructType(parameterTypes[i]);
			convertedParams[i] = mapper.readValue(paramJsonParser, paramJavaType);
		}

		// invoke the method
		Object result = m.invoke(target, convertedParams);
		return (m.getGenericReturnType() != null) ? mapper.valueToTree(result) : null;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		super.handle(request, response);
	}

}

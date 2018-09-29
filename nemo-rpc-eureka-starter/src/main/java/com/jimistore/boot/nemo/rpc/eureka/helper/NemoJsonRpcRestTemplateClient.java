package com.jimistore.boot.nemo.rpc.eureka.helper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.googlecode.jsonrpc4j.IJsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClient;
import com.googlecode.jsonrpc4j.JsonRpcClientException;
import com.jimistore.boot.nemo.core.helper.Context;

public class NemoJsonRpcRestTemplateClient extends JsonRpcClient implements IJsonRpcClient {
	
	private static final String JOIN_STR="-";
	
	RestTemplate restTemplate;
	
	URL serviceUrl;
	
	INemoRpcClusterExporter nemoRpcClusterExporter;
	
	ObjectMapper objectMapper;
	
	String path;
	
	String module;
	
	String version;
	
	String baseUrl;

	public NemoJsonRpcRestTemplateClient(ObjectMapper objectMapper) {
		super(objectMapper);
		this.objectMapper = objectMapper;
	}

	public NemoJsonRpcRestTemplateClient setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		return this;
	}

	public NemoJsonRpcRestTemplateClient setServiceUrl(URL serviceUrl) {
		this.serviceUrl = serviceUrl;
		return this;
	}

	@Override
	public void invoke(String methodName, Object argument) throws Throwable {
		restTemplate.postForEntity(getServiceUrl(), this.createRequestSelf(methodName, argument), Object.class);
	}

	@Override
	public Object invoke(String methodName, Object argument, Type returnType) throws Throwable {
		
		ResponseEntity<String> response = restTemplate.postForEntity(getServiceUrl(), this.createRequestSelf(methodName, argument), String.class);
		return this.parseResponse(returnType, response.getBody());
	}

	@Override
	public Object invoke(String methodName, Object argument, Type returnType, Map<String, String> extraHeaders)
			throws Throwable {
		return this.invoke(methodName, argument, returnType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T invoke(String methodName, Object argument, Class<T> clazz) throws Throwable {
		ResponseEntity<String> response = restTemplate.postForEntity(getServiceUrl(), this.createRequestSelf(methodName, argument), String.class);
		return (T) this.parseResponse(clazz, response.getBody());
	}

	@Override
	public <T> T invoke(String methodName, Object argument, Class<T> clazz, Map<String, String> extraHeaders)
			throws Throwable {
		return this.invoke(methodName, argument, clazz);
	}
	
	private String getServiceUrl() throws IOException{
		if(baseUrl==null){
			if(nemoRpcClusterExporter!=null){
				String instanceId = module;
				if(!StringUtils.isEmpty(version)){
					instanceId=new StringBuilder(module).append(JOIN_STR).append(version).toString();
				}
				baseUrl = nemoRpcClusterExporter.getNextServerUrl(instanceId);
				if(baseUrl==null||baseUrl.isEmpty()){
					throw new IOException(String.format("serviceUrl cannot be empty, check cluster server please ", baseUrl));
				}
			}else{
				baseUrl = this.getServiceUrl().toString();
			}
		}
		return new URL(new URL(baseUrl), path).toString();
	}

	public NemoJsonRpcRestTemplateClient setNemoRpcClusterExporter(INemoRpcClusterExporter nemoRpcClusterExporter) {
		this.nemoRpcClusterExporter = nemoRpcClusterExporter;
		return this;
	}

	public NemoJsonRpcRestTemplateClient setPath(String path) {
		this.path = path;
		return this;
	}

	public NemoJsonRpcRestTemplateClient setModule(String module) {
		this.module = module;
		return this;
	}

	public NemoJsonRpcRestTemplateClient setVersion(String version) {
		this.version = version;
		return this;
	}
	
	public NemoJsonRpcRestTemplateClient setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		return this;
	}

	private Object parseResponse(Type type,String json) throws Throwable{

		JsonNode response = objectMapper.readValue(json, JsonNode.class);

		// bail on invalid response
		if (!response.isObject()) {
			throw new JsonRpcClientException(0, "Invalid JSON-RPC response", response);
		}
		ObjectNode jsonObject = ObjectNode.class.cast(response);

		return super.readResponse(type, jsonObject);
	}
	
	private Object createRequestSelf(String methodName, Object argument){
		Object request = super.createRequest(methodName, argument);
		Object user = Context.get(Context.CONTEXT_REQUEST_USER);
		if(user!=null){
			HttpHeaders headers = new HttpHeaders();
			headers.add(Context.CONTEXT_REQUEST_USER, user.toString());
			return new HttpEntity<Object>(request, headers);
		
		}
		return request;
	}

}

package com.jimistore.boot.nemo.rpc.eureka.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class NemoJsonRpcHttpClient extends JsonRpcHttpClient {
	
	private static final String JOIN_STR="-";
	
	INemoRpcClusterExporter nemoRpcClusterExporter;
	
	String path;
	
	String module;
	
	String version;

	public NemoJsonRpcHttpClient(INemoRpcClusterExporter nemoRpcClusterExporter, ObjectMapper mapper, URL serviceUrl, String module, String version, String path, Map<String, String> headers) {
		super(mapper, serviceUrl, headers);
		this.path = path;
		this.nemoRpcClusterExporter=nemoRpcClusterExporter;
		this.module=module;
		this.version=version;
	}
	
	@Override
	protected HttpURLConnection prepareConnection(Map<String, String> extraHeaders)
		throws IOException {
		String baseUrl = null;
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
		this.setServiceUrl(new URL(new URL(baseUrl), path));
		return super.prepareConnection(extraHeaders);
	}

	public void setModule(String module) {
		this.module = module;
	}

	public void setNemoRpcClusterExporter(INemoRpcClusterExporter nemoRpcClusterExporter) {
		this.nemoRpcClusterExporter = nemoRpcClusterExporter;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
}

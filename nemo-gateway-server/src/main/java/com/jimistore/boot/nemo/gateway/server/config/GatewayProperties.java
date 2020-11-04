package com.jimistore.boot.nemo.gateway.server.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nemo.gateway")
public class GatewayProperties {

	private Map<String, RouteItem> routes;

	private long connectTimeout;

	private long readTimeout;

	private long fuseTimeout;

	public Map<String, RouteItem> getRoutes() {
		return routes;
	}

	public GatewayProperties setRoutes(Map<String, RouteItem> routes) {
		this.routes = routes;
		return this;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	public GatewayProperties setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public long getReadTimeout() {
		return readTimeout;
	}

	public GatewayProperties setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public long getFuseTimeout() {
		return fuseTimeout;
	}

	public GatewayProperties setFuseTimeout(long fuseTimeout) {
		this.fuseTimeout = fuseTimeout;
		return this;
	}

	public class RouteItem {

		String id;

		String path;

		String url;

		public String getId() {
			return id;
		}

		public RouteItem setId(String id) {
			this.id = id;
			return this;
		}

		public String getPath() {
			return path;
		}

		public RouteItem setPath(String path) {
			this.path = path;
			return this;
		}

		public String getUrl() {
			return url;
		}

		public RouteItem setUrl(String url) {
			this.url = url;
			return this;
		}

	}

}

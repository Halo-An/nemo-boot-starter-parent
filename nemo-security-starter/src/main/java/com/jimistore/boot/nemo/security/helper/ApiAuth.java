package com.jimistore.boot.nemo.security.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.jimistore.boot.nemo.security.exception.UnauthorizedException;

public class ApiAuth implements IApiAuth {

	String[] ignoreMethod;

	String[] ignoreMatch;

	private Map<String, ApiAuthConfig> secretMap = new HashMap<String, ApiAuthConfig>();

	@Value("${auth.ignore.match:}")
	public ApiAuth setIgnoreMatchStr(String ignoreMatchStr) {
		this.ignoreMatch = StringUtil.split(ignoreMatchStr, StringUtil.SPLIT_STR);
		return this;
	}

	public ApiAuth setSecretMap(Map<String, ApiAuthConfig> secretMap) {
		this.secretMap = secretMap;
		return this;
	}

	@Override
	public String getSecret(String appid) {
		if (appid != null) {
			return secretMap.get(appid).getSecret();
		}
		throw new UnauthorizedException();
	}

	@Override
	public boolean isEmpty() {

		return secretMap.size() == 0;
	}

	public String[] getIgnoreMethod() {
		return ignoreMethod;
	}

	public ApiAuth setIgnoreMethod(String[] ignoreMethod) {
		this.ignoreMethod = ignoreMethod;
		return this;
	}

	@Override
	public String[] getMatch(String appid) {
		if (appid == null) {
			return null;
		}
		if (!secretMap.containsKey(appid)) {
			return null;
		}
		return secretMap.get(appid).getMatch();
	}

	@Override
	public String[] getIgnoreMatch() {
		return ignoreMatch;
	}

	public ApiAuth setIgnoreMatch(String[] ignoreMatch) {
		this.ignoreMatch = ignoreMatch;
		return this;
	}

	public static class ApiAuthConfig {
		String appid;

		String secret;

		String[] match;

		public String getAppid() {
			return appid;
		}

		public ApiAuthConfig setAppid(String appid) {
			this.appid = appid;
			return this;
		}

		public String getSecret() {
			return secret;
		}

		public ApiAuthConfig setSecret(String secret) {
			this.secret = secret;
			return this;
		}

		public String[] getMatch() {
			return match;
		}

		public ApiAuthConfig setMatch(String[] match) {
			this.match = match;
			return this;
		}
	}
}
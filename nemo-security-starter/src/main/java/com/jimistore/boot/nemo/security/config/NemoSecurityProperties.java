package com.jimistore.boot.nemo.security.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nemo.auth")
public class NemoSecurityProperties {

	Map<String, SecurityItem> sign = new HashMap<>();

	Token token;

	public static class Token {

		String appid;

		String secret;

		String encryptType;

		String publicKey;

		String privateKey;

		int timeout;

		boolean ignoreAppId;

		String ignoreMatch;

		String match;

		String ignoreOSMatch;

		public String getAppid() {
			return appid;
		}

		public Token setAppid(String appid) {
			this.appid = appid;
			return this;
		}

		public String getSecret() {
			return secret;
		}

		public Token setSecret(String secret) {
			this.secret = secret;
			return this;
		}

		public String getEncryptType() {
			return encryptType;
		}

		public Token setEncryptType(String encryptType) {
			this.encryptType = encryptType;
			return this;
		}

		public String getPublicKey() {
			return publicKey;
		}

		public Token setPublicKey(String publicKey) {
			this.publicKey = publicKey;
			return this;
		}

		public String getPrivateKey() {
			return privateKey;
		}

		public Token setPrivateKey(String privateKey) {
			this.privateKey = privateKey;
			return this;
		}

		public int getTimeout() {
			return timeout;
		}

		public Token setTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public boolean isIgnoreAppId() {
			return ignoreAppId;
		}

		public Token setIgnoreAppId(boolean ignoreAppId) {
			this.ignoreAppId = ignoreAppId;
			return this;
		}

		public String getIgnoreMatch() {
			return ignoreMatch;
		}

		public Token setIgnoreMatch(String ignoreMatch) {
			this.ignoreMatch = ignoreMatch;
			return this;
		}

		public String getMatch() {
			return match;
		}

		public Token setMatch(String match) {
			this.match = match;
			return this;
		}

		public String getIgnoreOSMatch() {
			return ignoreOSMatch;
		}

		public Token setIgnoreOSMatch(String ignoreOSMatch) {
			this.ignoreOSMatch = ignoreOSMatch;
			return this;
		}
	}

	public static class SecurityItem {
		String secret;

		String match;

		public String getSecret() {
			return secret;
		}

		public SecurityItem setSecret(String secret) {
			this.secret = secret;
			return this;
		}

		public String getMatch() {
			return match;
		}

		public SecurityItem setMatch(String match) {
			this.match = match;
			return this;
		}

	}

}

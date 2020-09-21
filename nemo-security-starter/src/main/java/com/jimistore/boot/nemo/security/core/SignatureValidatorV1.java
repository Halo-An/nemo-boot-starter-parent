package com.jimistore.boot.nemo.security.core;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;
import com.jimistore.boot.nemo.security.exception.UnauthorizedException;
import com.jimistore.boot.nemo.security.helper.IApiAuth;
import com.jimistore.util.format.string.MD5Util;

public class SignatureValidatorV1 implements ISignatureValidator {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureValidatorV1.class);

	private IApiAuth apiAuth;

	public SignatureValidatorV1 setApiAuth(IApiAuth apiAuth) {
		this.apiAuth = apiAuth;
		return this;
	}

	@Override
	public boolean isMatch(HttpServletRequest request) {
		// 判断是否是本版本的签名
		String sign = request.getHeader("signature");
		if (sign != null && sign.trim().length() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void check(HttpServletRequest request) throws SignatureInvalidException {

		// 如果未开启配置或命中忽略策略
		if (apiAuth == null || apiAuth.isEmpty() || this.checkIgnore(request)) {
			return;
		}

		try {
			String url = request.getRequestURI().toString();
			AntPathMatcher matcher = new AntPathMatcher();

			String[] ignoreMatchs = apiAuth.getIgnoreMatch();
			if (ignoreMatchs != null) {
				String[] arrayOfString1;
				int j = (arrayOfString1 = ignoreMatchs).length;
				for (int i = 0; i < j; i++) {
					String matchStr = arrayOfString1[i];
					if ((matchStr.trim().length() > 0) && (matcher.match(matchStr, url))) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(String.format("hit ignore strategy, the match is %s, url is %s ",
									new Object[] { matchStr, url }));
						}
						return;
					}
				}
			}
			String appid = request.getHeader("appId");

			String[] matchs = apiAuth.getMatch(appid);
			boolean flag = false;
			if (matchs != null) {
				String[] arrayOfString2;
				int m = (arrayOfString2 = matchs).length;
				for (int k = 0; k < m; k++) {
					String matchStr = arrayOfString2[k];
					if ((matchStr.trim().length() > 0) && (matcher.match(matchStr, url))) {
						if (LOG.isDebugEnabled()) {
							LOG.debug(String.format("hit match of authorised api, the match is %s, url is %s ",
									new Object[] { matchStr, url }));
						}
						flag = true;
					}
				}
			}
			if (!flag) {
				throw new UnauthorizedException("don't have authorization to access");
			}
			String signature = request.getHeader("signature");
			Long timestamp = Long.valueOf(Long.parseLong(request.getHeader("timestamp")));
			String device = request.getHeader("deviceId");
			String os = request.getHeader("OSVersion");
			String userid = request.getHeader("userId");
			if (userid == null) {
				userid = "";
			}
			String password = apiAuth.getSecret(appid);
			String[] sources = new String[6];
			sources[0] = (appid + "=" + "appId");
			sources[1] = (password + "=" + "password");
			sources[2] = (timestamp + "=" + "timestamp");
			sources[3] = (device + "=" + "deviceId");
			sources[4] = (os + "=" + "OSVersion");
			sources[5] = (userid + "=" + "userId");
			Arrays.sort(sources);
			String sortStr = sources[0] + "&" + sources[1] + "&" + sources[2] + "&" + sources[3] + "&" + sources[4]
					+ "&" + sources[5];

			String signatureServer = MD5Util.Bit32(sortStr);
			if (signature.toUpperCase().equals(signatureServer.toUpperCase())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(String.format("signature check success, the signature is %s, url is %s ",
							new Object[] { signature, url }));
				}
				return;
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("signature check failed, the correct signature is %s, the error is %s ",
						new Object[] { signature, url }));
			}
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(e.getMessage());
			}
			throw new SignatureInvalidException();
		}
		throw new UnauthorizedException("don't have authorization to access");
	}

	private boolean checkIgnore(HttpServletRequest request) {
		String url = request.getRequestURI().toString();
		AntPathMatcher matcher = new AntPathMatcher();

		// 匹配忽略url
		String[] ignoreMatchs = apiAuth.getIgnoreMatch();
		if (ignoreMatchs != null) {
			for (String matchStr : ignoreMatchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, url)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("hit ignore strategy, the match is %s, url is %s ", matchStr, url));
					}
					return true;
				}
			}
		}
		return false;
	}
}

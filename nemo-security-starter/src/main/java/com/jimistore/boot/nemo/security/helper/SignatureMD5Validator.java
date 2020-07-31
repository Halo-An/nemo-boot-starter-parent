package com.jimistore.boot.nemo.security.helper;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import com.jimistore.boot.nemo.core.helper.HttpServletRequestProxy;
import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;
import com.jimistore.util.format.collection.MapUtil;
import com.jimistore.util.format.exception.SignException;
import com.jimistore.util.format.string.SecurityUtil;

public class SignatureMD5Validator implements ISignatureValidator {

	private final Logger log = Logger.getLogger(getClass());

	private IApiAuth apiAuth;

	private int timeDev = 300;

	@Value("${auth.time.timeout:300}")
	public SignatureMD5Validator setTimeDev(int timeDev) {
		this.timeDev = timeDev;
		return this;
	}

	public SignatureMD5Validator setApiAuth(IApiAuth apiAuth) {
		this.apiAuth = apiAuth;
		return this;
	}

	@Override
	public String getSignType() {
		return Constant.SIGN_TYPE_MD5;
	}

	@Override
	public void check(HttpServletRequest request) throws SignatureInvalidException {
		String signType = request.getHeader(Constant.SIGN_TYPE);
		// 判断是否是本版本的签名
		if (!Constant.SIGN_TYPE_MD5.equals(signType)) {
			throw new SignatureInvalidException();
		}
		// 判断配置是否生效
		if (apiAuth == null || apiAuth.isEmpty()) {
			throw new SignatureInvalidException();
		}
		// 判断是否命中忽略策略
		if (!this.checkIgnore(request)) {
			// 判断访问的资源是否有权限
			this.checkUrl(request);
			// 判断访问是否过期
			this.checkTime(request);
			// 判断签名是否合法
			this.checkSign(request);
		}
	}

	/**
	 * 判断是否命中忽略策略
	 * 
	 * @param request
	 * @return
	 */
	private boolean checkIgnore(HttpServletRequest request) {
		String url = request.getRequestURI().toString();
		AntPathMatcher matcher = new AntPathMatcher();

		// 匹配忽略url
		String[] ignoreMatchs = apiAuth.getIgnoreMatch();
		if (ignoreMatchs != null) {
			for (String matchStr : ignoreMatchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, url)) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("hit ignore strategy, the match is %s, url is %s ", matchStr, url));
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 校验访问路径是否有权限
	 * 
	 * @param url
	 * @return
	 */
	private void checkUrl(HttpServletRequest request) {

		String url = request.getRequestURI().toString();
		AntPathMatcher matcher = new AntPathMatcher();

		String appid = request.getHeader(Constant.APPID);
		// 匹配可访问的范围
		String[] matchs = apiAuth.getMatch(appid);
		boolean flag = false;
		if (matchs != null) {
			for (String matchStr : matchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, url)) {
					if (log.isDebugEnabled()) {
						log.debug(String.format("hit match of authorised api, the match is %s, url is %s ", matchStr,
								url));
					}
					flag = true;
				}
			}
		}

		if (!flag) {
			throw new SignatureInvalidException();
		}
	}

	/**
	 * 校验时效
	 * 
	 * @param timestamp
	 * @return
	 */
	private void checkTime(HttpServletRequest request) {
		Long timestamp = 0L;
		try {
			timestamp = Long.parseLong(request.getHeader(Constant.TIMESTAMP));
		} catch (Exception e) {
			throw new SignatureInvalidException(e);
		}
		// 校验时间戳
		long now = System.currentTimeMillis() / 1000;
		if (Math.abs(now - timestamp) > timeDev) {
			throw new SignatureInvalidException();
		}
	}

	/**
	 * 校验签名
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void checkSign(HttpServletRequest request) {
		String url = request.getRequestURI().toString();
		String signature = request.getHeader(Constant.SIGN);
		Long timestamp = Long.parseLong(request.getHeader(Constant.TIMESTAMP));
		String token = request.getHeader(Constant.TOKEN);
		String appid = request.getHeader(Constant.APPID);
		String password = apiAuth.getSecret(appid);
		String body = this.getBody(request);

		try {
			String signatureServer = SecurityUtil.SignMD5(MapUtil.hasMap(Constant.APPID, appid, Constant.TIMESTAMP,
					timestamp, Constant.SECRET, password, Constant.TOKEN, token, Constant.BODY, body));
			// 校验签名
			if (signature.toUpperCase().equals(signatureServer.toUpperCase())) {
				if (log.isDebugEnabled()) {
					log.debug(
							String.format("signature check success, the signature is %s, url is %s ", signature, url));
				}
				return;
			}
		} catch (SignException e) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("signature check failed, the correct signature is %s, the error is %s ",
						signature, e.getMessage()));
				log.debug(e);
			}
			throw new SignatureInvalidException(e);
		}
		throw new SignatureInvalidException();
	}

	private String getBody(HttpServletRequest request) {
		String body = null;
		if ((request instanceof HttpServletRequestProxy)) {
			body = ((HttpServletRequestProxy) request).getBody();
		}
		if (body == null) {
			try {
				StringBuilder sb = new StringBuilder();
				BufferedReader br = request.getReader();
				String str;
				while ((str = br.readLine()) != null) {
					if (sb.length() > 0) {
						sb.append("\n");
					}
					sb.append(str);
				}
				if (sb.length() > 0) {
					body = sb.toString();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return body;

	}

}

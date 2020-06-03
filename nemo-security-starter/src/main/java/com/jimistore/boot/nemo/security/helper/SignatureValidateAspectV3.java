package com.jimistore.boot.nemo.security.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jimistore.boot.nemo.core.helper.HttpServletRequestProxy;
import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;
import com.jimistore.util.format.collection.MapUtil;
import com.jimistore.util.format.exception.SignException;
import com.jimistore.util.format.string.SecurityUtil;

@Aspect
@Order(100)
public class SignatureValidateAspectV3 {

	private final Logger log = Logger.getLogger(getClass());
	public static final String APPID = "appId";
	public static final String PASSWORD = "password";
	public static final String TIMESTAMP = "timestamp";
	public static final String SIGNATURE = "sign";
	public static final String SIGN_TYPE = "signType";
	public static final String SIGN_TYPE_MINE = "MD5";
	public static final String TOKEN = "token";
	public static final String BODY = "body";

	private IApiAuth apiAuth;

	Map<String, Integer> timeErrMap = new HashMap<String, Integer>();

	private int timeDev = 120;

	@Value("${auth.time.timeout:120}")
	public SignatureValidateAspectV3 setTimeDev(int timeDev) {
		this.timeDev = timeDev;
		return this;
	}

	public SignatureValidateAspectV3 setApiAuth(IApiAuth apiAuth) {
		this.apiAuth = apiAuth;
		return this;
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping)")
	public void auth() {
	}

	@Before("auth()")
	public void before(JoinPoint joinPoint) throws Throwable {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		// 判断是否是本版本的签名
		String signType = request.getHeader(SIGN_TYPE);
		if (!SIGN_TYPE_MINE.equals(signType)) {
			return;
		}

		// 如果未开启配置或命中忽略策略
		if (apiAuth == null || apiAuth.isEmpty() || this.checkIgnore(request)) {
			return;
		}

		this.checkUrl(request);
		this.checkTime(request);
		this.checkSign(request);

	}

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

		String appid = request.getHeader(APPID);
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
		Long timestamp = Long.parseLong(request.getHeader(TIMESTAMP));
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
		String signature = request.getHeader(SIGNATURE);
		Long timestamp = Long.parseLong(request.getHeader(TIMESTAMP));
		String token = request.getHeader(TOKEN);
		String appid = request.getHeader(APPID);
		String password = apiAuth.getSecret(appid);
		String body = this.getBody(request);

		try {
			String signatureServer = SecurityUtil.SignMD5(
					MapUtil.hasMap(APPID, appid, TIMESTAMP, timestamp, PASSWORD, password, TOKEN, token, BODY, body));
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
		return Base64.encodeBase64String(body.getBytes());

	}

}

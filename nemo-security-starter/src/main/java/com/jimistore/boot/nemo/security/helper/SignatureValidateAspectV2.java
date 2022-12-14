package com.jimistore.boot.nemo.security.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jimistore.boot.nemo.core.helper.HttpServletRequestProxy;
import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

@Aspect
@Order(100)
public class SignatureValidateAspectV2 {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	public static final String APPID = "appId";
	public static final String PASSWORD = "password";
	public static final String TIMESTAMP = "timestamp";
	public static final String SIGNATURE = "sign";
	public static final String SIGN_TYPE = "signType";
	public static final String OLD_SIGN = "signature";
	public static final String DEVICE = "deviceId";
	public static final String OS = "OSVersion";
	public static final String TOKEN = "token";
	public static final String USERID = "userId";
	public static final String BODY = "body";

	private IApiAuth apiAuth;

	Map<String, Integer> timeErrMap = new HashMap<String, Integer>();

	private int maxTimeErr = 100;

	private int timeDev = 120;

	@Value("${auth.time.max:100}")
	public SignatureValidateAspectV2 setMaxTimeErr(int maxTimeErr) {
		this.maxTimeErr = maxTimeErr;
		return this;
	}

	@Value("${auth.time.timeout:120}")
	public SignatureValidateAspectV2 setTimeDev(int timeDev) {
		this.timeDev = timeDev;
		return this;
	}

	public SignatureValidateAspectV2 setApiAuth(IApiAuth apiAuth) {
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

		// ??????????????????????????????????????????
		if (apiAuth == null || apiAuth.isEmpty() || this.checkIgnore(request)) {
			return;
		}

		// ?????????????????????????????????
		String signature = request.getHeader(SIGNATURE);
		String oldSign = request.getHeader(OLD_SIGN);
		String signType = request.getHeader(SIGN_TYPE);
		if (signature == null && oldSign != null) {
			return;
		}
		if (signType != null && signType.length() > 0) {
			return;
		}

		this.checkUrl(request);
		this.checkTime(request);
		this.checkSign(request);

	}

	private boolean checkIgnore(HttpServletRequest request) {
		String url = request.getRequestURI().toString();
		AntPathMatcher matcher = new AntPathMatcher();

		// ????????????url
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

	/**
	 * ?????????????????????????????????
	 * 
	 * @param url
	 * @return
	 */
	private void checkUrl(HttpServletRequest request) {

		String url = request.getRequestURI().toString();
		AntPathMatcher matcher = new AntPathMatcher();

		String appid = request.getHeader(APPID);
		// ????????????????????????
		String[] matchs = apiAuth.getMatch(appid);
		boolean flag = false;
		if (matchs != null) {
			for (String matchStr : matchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, url)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("hit match of authorised api, the match is %s, url is %s ", matchStr,
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
	 * ????????????
	 * 
	 * @param timestamp
	 * @return
	 */
	private void checkTime(HttpServletRequest request) {
		Long timestamp = Long.parseLong(request.getHeader(TIMESTAMP));
		String device = request.getHeader(DEVICE);
		// ???????????????
		long now = System.currentTimeMillis() / 1000;
		if (Math.abs(now - timestamp) > timeDev) {
			Integer errNum = timeErrMap.get(device);
			// ???????????????????????????????????????
			if (errNum != null && errNum > maxTimeErr) {
				throw new SignatureInvalidException();
			}
			if (errNum == null) {
				errNum = 0;
			} else {
				errNum = errNum + 1;
			}
			timeErrMap.put(device, errNum);
		}
	}

	/**
	 * ????????????
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void checkSign(HttpServletRequest request) {
		String url = request.getRequestURI().toString();
		String signature = request.getHeader(SIGNATURE);
		Long timestamp = Long.parseLong(request.getHeader(TIMESTAMP));
		String device = request.getHeader(DEVICE);
		String os = request.getHeader(OS);
		String token = request.getHeader(TOKEN);
		String appid = request.getHeader(APPID);
		String password = apiAuth.getSecret(appid);
		String userid = request.getHeader(USERID);
		String body = this.getBody(request);

		if (userid == null) {
			userid = "";
		}

		try {
			String signatureServer = SecurityUtil.SignMD5(MapUtil.hasMap(APPID, appid, TIMESTAMP, timestamp, DEVICE,
					device, OS, os, USERID, userid, PASSWORD, password, TOKEN, token, BODY, body));
			// ????????????
			if (signature.toUpperCase().equals(signatureServer.toUpperCase())) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(
							String.format("signature check success, the signature is %s, url is %s ", signature, url));
				}
				return;
			}
		} catch (SignatureInvalidException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("signature check failed, the correct signature is %s, the error is %s ",
						signature, e.getMessage()));
				LOG.debug(e.getMessage(), e);
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

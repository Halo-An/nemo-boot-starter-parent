package com.jimistore.boot.nemo.security.helper;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;
import com.jimistore.boot.nemo.security.exception.UnauthorizedException;
import com.jimistore.util.format.string.MD5Util;

@Aspect
@Order(101)
public class SignatureValidateAspectV1 {
	private final Logger log = Logger.getLogger(getClass());
	public static final String APPID = "appId";
	public static final String PASSWORD = "password";
	public static final String TIMESTAMP = "timestamp";
	public static final String SIGNATURE = "signature";
	public static final String NEW_SIGNATURE = "sign";
	public static final String DEVICE = "deviceId";
	public static final String OS = "OSVersion";
	public static final String USERID = "userId";
	public static final int dev = 120;
	private IApiAuth apiAuth;

	public SignatureValidateAspectV1 setApiAuth(IApiAuth apiAuth) {
		this.apiAuth = apiAuth;
		return this;
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping)")
	public void authOld() {
	}

	@Before("authOld()")
	public void before(JoinPoint joinPoint) throws Throwable {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		// 如果未开启配置或命中忽略策略
		if (apiAuth == null || apiAuth.isEmpty() || this.checkIgnore(request)) {
			return;
		}

		String newSign = request.getHeader("sign");
		if (newSign != null) {
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
						if (log.isDebugEnabled()) {
							log.debug(String.format("hit ignore strategy, the match is %s, url is %s ",
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
						if (log.isDebugEnabled()) {
							log.debug(String.format("hit match of authorised api, the match is %s, url is %s ",
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
				if (log.isDebugEnabled()) {
					log.debug(String.format("signature check success, the signature is %s, url is %s ",
							new Object[] { signature, url }));
				}
				return;
			}
			if (log.isDebugEnabled()) {
				log.debug(String.format("signature check failed, the correct signature is %s, the error is %s ",
						new Object[] { signature, url }));
			}
		} catch (Exception e) {
			log.debug(e);
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
					if (log.isDebugEnabled()) {
						log.debug(String.format("hit ignore strategy, the match is %s, url is %s ", matchStr, url));
					}
					return true;
				}
			}
		}
		return false;
	}
}
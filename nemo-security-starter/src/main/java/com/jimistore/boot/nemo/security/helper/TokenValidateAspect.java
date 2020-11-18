package com.jimistore.boot.nemo.security.helper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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

import com.jimistore.boot.nemo.core.helper.Context;
import com.jimistore.boot.nemo.security.exception.TokenInvalidException;
import com.jimistore.boot.nemo.security.request.IDeviceAuthRequest;
import com.jimistore.boot.nemo.security.request.IUserAuthRequest;

@Aspect
@Order(100)
public class TokenValidateAspect {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	public static final String TOKEN = "token";
	public static final String USERID = "userId";
	public static final String DEVICE = "deviceId";
	public static final String OS = "OSVersion";
	public static final int dev = 120;

	private String[] ignoreMatchs;

	private String[] matchs;

	private String[] OSMatchs;

	private ITokenFactory tokenFactory;

	public TokenValidateAspect setTokenFactory(ITokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
		return this;
	}

	@Value("${token.ignore.match:}")
	public TokenValidateAspect setIgnoreMatchStr(String ignoreMatchStr) {
		this.ignoreMatchs = StringUtil.split(ignoreMatchStr, StringUtil.SPLIT_STR);
		return this;
	}

	@Value("${token.match}")
	public TokenValidateAspect setMatchStr(String matchStr) {
		this.matchs = StringUtil.split(matchStr, StringUtil.SPLIT_STR);
		return this;
	}

	@Value("${token.ignore.os.match:}")
	public TokenValidateAspect setOSMatchStr(String matchStr) {
		this.OSMatchs = StringUtil.split(matchStr, StringUtil.SPLIT_STR);
		return this;
	}

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void auth() {
	}

	@Before("auth()")
	public void before(JoinPoint joinPoint) throws Throwable {

		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		String userid = request.getHeader(USERID);
		Context.put(Context.CONTEXT_REQUEST_USER, userid);

		// 如果命中忽略策略
		if (this.checkIgnore(request)) {
			return;
		}

		this.checkToken(joinPoint, request);

	}

	private boolean checkIgnore(HttpServletRequest request) {
		String url = request.getRequestURI().toString();
		AntPathMatcher matcher = new AntPathMatcher();

		boolean flag = true;
		// 匹配是否是验证范围
		if (matchs != null) {
			for (String matchStr : matchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, url)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("hit check strategy, the match is %s, url is %s ", matchStr, url));
					}
					flag = false;
				}
			}
		}

		// 匹配忽略url
		if (!flag && ignoreMatchs != null) {
			for (String matchStr : ignoreMatchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, url)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("hit ignore of url strategy, the match is %s, url is %s ", matchStr,
								url));
					}
					flag = true;
				}
			}
		}

		// 匹配忽略os
		if (!flag && OSMatchs != null) {
			String os = request.getHeader(OS);
			for (String matchStr : OSMatchs) {
				if (matchStr.trim().length() > 0 && matcher.match(matchStr, os)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(
								String.format("hit ignore of os strategy, the match is %s, url is %s ", matchStr, url));
					}
					flag = true;
				}
			}

		}
		return flag;
	}

	private String getTop(String... strs) {
		for (String str : strs) {
			if (str != null) {
				return str;
			}
		}
		return null;
	}

	private void checkToken(JoinPoint joinPoint, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		Map<String, String> map = new HashMap<String, String>();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}
		String device = this.getTop(map.get(DEVICE), request.getHeader(DEVICE), request.getParameter(DEVICE));
		String token = this.getTop(map.get(TOKEN), request.getHeader(TOKEN), request.getParameter(TOKEN));
		String userid = this.getTop(map.get(USERID), request.getHeader(USERID), request.getParameter(USERID));

		for (Object arg : joinPoint.getArgs()) {
			if (arg instanceof IUserAuthRequest) {
				IUserAuthRequest userRequest = (IUserAuthRequest) arg;
				if (userRequest.getUserId() == null || !userRequest.getUserId().equals(userid)) {
					throw new TokenInvalidException();
				}
			}

			if (arg instanceof IDeviceAuthRequest) {
				IDeviceAuthRequest deviceRequest = (IDeviceAuthRequest) arg;
				if (deviceRequest.getDeviceId() == null || !deviceRequest.getDeviceId().equals(device)) {
					throw new TokenInvalidException();
				}
			}
		}

		tokenFactory.check(userid, device, token);

	}

}

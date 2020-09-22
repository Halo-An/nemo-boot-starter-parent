package com.jimistore.boot.nemo.security.core;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;
import com.jimistore.boot.nemo.security.helper.Constant;
import com.jimistore.boot.nemo.security.helper.IApiAuth;

public class SignatureDefaultValidator implements ISignatureValidator {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureDefaultValidator.class);

	private IApiAuth apiAuth;

	public SignatureDefaultValidator setApiAuth(IApiAuth apiAuth) {
		this.apiAuth = apiAuth;
		return this;
	}

	@Override
	public boolean isMatch(HttpServletRequest request) {
		// 判断是否是本版本的签名
		String appId = request.getHeader(Constant.APPID);
		String sign = request.getHeader(Constant.SIGN);
		if (appId == null && sign == null) {
			return true;
		}
		return false;
	}

	@Override
	public void check(HttpServletRequest request) throws SignatureInvalidException {

		// 判断配置是否生效
		if (apiAuth == null || apiAuth.isEmpty()) {
			throw new SignatureInvalidException();
		}

		// 判断是否忽略
		if (this.checkIgnore(request)) {
			return;
		}
		throw new SignatureInvalidException();
	}

	/**
	 * 判断是否命中忽略策略
	 * 
	 * @param request
	 * @return
	 */
	private boolean checkIgnore(HttpServletRequest request) {

		// 匹配忽略的method
		String[] ignoreMethods = apiAuth.getIgnoreMethod();
		if (ignoreMethods != null && ignoreMethods.length > 0) {
			String method = request.getMethod();
			for (String ignoreMethod : ignoreMethods) {
				if (method != null && method.equals(ignoreMethod)) {
					return true;
				}
			}
		}

		// 匹配忽略url
		String[] ignoreMatchs = apiAuth.getIgnoreMatch();
		if (ignoreMatchs != null) {
			AntPathMatcher matcher = new AntPathMatcher();
			String url = request.getRequestURI().toString();
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

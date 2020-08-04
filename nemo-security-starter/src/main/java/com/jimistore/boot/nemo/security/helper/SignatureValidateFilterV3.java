package com.jimistore.boot.nemo.security.helper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

import com.jimistore.boot.nemo.core.response.Response;
import com.jimistore.boot.nemo.core.util.JsonString;
import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

@Order(100)
@WebFilter(urlPatterns = "/*", filterName = "SignatureValidateFilter")
public class SignatureValidateFilterV3 implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureValidateFilterV3.class);

	private Map<String, ISignatureValidator> signValidatorMap = new HashMap<>();

	private IApiAuth apiAuth;

	public SignatureValidateFilterV3 setApiAuth(IApiAuth apiAuth) {
		this.apiAuth = apiAuth;
		return this;
	}

	public SignatureValidateFilterV3 setSignValidatorMap(Map<String, ISignatureValidator> signValidatorMap) {
		this.signValidatorMap = signValidatorMap;
		return this;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		// 校验是否需要忽略
		if (!this.checkIgnore(request)) {
			String signType = request.getHeader(Constant.SIGN_TYPE);
			try {
				if (signType == null || signType.trim().length() == 0) {
					LOG.warn(String.format("sign type can not be null, the uri is %s, method is %s",
							request.getRequestURI(), request.getMethod()));
					throw new SignatureInvalidException();
				}
				ISignatureValidator validator = signValidatorMap.get(signType);
				if (validator == null) {
					LOG.warn(String.format("validator can not be null, the uri is %s, method is %s",
							request.getRequestURI(), request.getMethod()));
					throw new SignatureInvalidException();
				}
				validator.check(request);
			} catch (Exception e) {
				Response<?> re = Response.error(e);
				resp.setCharacterEncoding("utf-8");
				resp.setContentType("application/json");
				resp.getWriter().print(JsonString.toJson(re));
				return;
			}
		}
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {

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

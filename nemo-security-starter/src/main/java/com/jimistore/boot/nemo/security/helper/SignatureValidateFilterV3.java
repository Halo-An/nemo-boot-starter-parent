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

import org.springframework.core.annotation.Order;

import com.jimistore.boot.nemo.core.response.Response;
import com.jimistore.boot.nemo.core.util.JsonString;
import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

@Order(100)
@WebFilter(urlPatterns = "/*", filterName = "SignatureValidateFilter")
public class SignatureValidateFilterV3 implements Filter {

	private Map<String, ISignatureValidator> signValidatorMap = new HashMap<>();

	public SignatureValidateFilterV3 setSignValidatorMap(Map<String, ISignatureValidator> signValidatorMap) {
		this.signValidatorMap = signValidatorMap;
		return this;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;

		String signType = request.getHeader(Constant.SIGN_TYPE);
		try {
			if (signType == null || signType.trim().length() == 0) {
				throw new SignatureInvalidException();
			}
			ISignatureValidator validator = signValidatorMap.get(signType);
			if (validator == null) {
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

		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}

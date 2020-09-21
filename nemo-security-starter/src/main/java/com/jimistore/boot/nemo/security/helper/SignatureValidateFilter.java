package com.jimistore.boot.nemo.security.helper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

import com.jimistore.boot.nemo.core.response.Response;
import com.jimistore.boot.nemo.core.util.JsonString;
import com.jimistore.boot.nemo.security.core.ISignatureValidator;
import com.jimistore.boot.nemo.security.exception.SignatureInvalidException;

@Order(100)
@WebFilter(urlPatterns = "/*", filterName = "SignatureValidateFilter")
public class SignatureValidateFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(SignatureValidateFilter.class);

	private Set<ISignatureValidator> signValidatorSet = new HashSet<>();

	public SignatureValidateFilter setSignValidatorSet(Set<ISignatureValidator> signValidatorSet) {
		this.signValidatorSet = signValidatorSet;
		return this;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		try {
			ISignatureValidator validator = null;
			for (ISignatureValidator signatureValidator : signValidatorSet) {
				if (signatureValidator.isMatch(request)) {
					validator = signatureValidator;
					break;
				}
			}
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
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {

	}

}

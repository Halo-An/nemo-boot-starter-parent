package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

@Order(10)
@WebFilter(urlPatterns = "/*", filterName = "CharsetFilter")
public class CharsetFilter implements Filter {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value("${nemo.charset:utf-8}")
	String encoding;

	@Value("${nemo.content-type:application/json}")
	String contentType;

	@Value("${nemo.charset.filter-pattern:/api/**}")
	String pattern;

	AntPathMatcher matcher = new AntPathMatcher();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (matcher.match(pattern, HttpServletRequest.class.cast(request).getRequestURI())) {
			response.setCharacterEncoding(encoding);
			response.setContentType(contentType);
			if (LOG.isDebugEnabled()) {
				LOG.debug("request doFilter");
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}

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
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.util.AntPathMatcher;

@Order(2)
@WebFilter(urlPatterns = "/*", filterName = "CorsFilter")
public class CorsFilter implements Filter {

	private static final String METHOD_OPTION = "OPTIONS";
	private static final String ORIGIN = "ORIGIN";

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Value("${nemo.cors.open-match:false}")
	boolean openMatch;

	@Value("${nemo.cors.url-match:/**}")
	String urlMatch;

	@Value("${nemo.cors.origin-match:**}")
	String originMatch;

	AntPathMatcher matcher = new AntPathMatcher();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = HttpServletRequest.class.cast(request);
		HttpServletResponse rep = HttpServletResponse.class.cast(response);

		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("request doFilter, origin is %s, uri is %s, method is %s", req.getHeader(ORIGIN),
					req.getRequestURI(), req.getMethod()));
		}

		if (!openMatch || (matcher.match(originMatch, req.getHeader(ORIGIN))
				&& matcher.match(urlMatch, req.getRequestURI()))) {
			rep.setHeader("Access-Control-Allow-Origin", "*");
			rep.setHeader("Access-Control-Allow-Methods", "*");
			rep.setHeader("Access-Control-Allow-Headers", "*");
			if (METHOD_OPTION.equals(req.getMethod())) {
				rep.setStatus(204);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
}

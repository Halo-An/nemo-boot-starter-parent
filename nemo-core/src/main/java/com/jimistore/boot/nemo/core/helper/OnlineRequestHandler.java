package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.jimistore.boot.nemo.core.api.service.OnlineHandler;

public class OnlineRequestHandler implements HttpRequestHandler {

	Set<OnlineHandler> onlineSet;

	public OnlineRequestHandler setOnlineSet(Set<OnlineHandler> onlineSet) {
		this.onlineSet = onlineSet;
		return this;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (onlineSet != null) {
			for (OnlineHandler online : onlineSet) {
				online.online();
			}
		}
		response.getWriter().print("{\"code\":\"200\",\"message\":\"success\"}");
	}

}

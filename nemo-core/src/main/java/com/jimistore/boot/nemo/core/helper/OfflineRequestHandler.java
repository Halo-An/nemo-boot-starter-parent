package com.jimistore.boot.nemo.core.helper;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.jimistore.boot.nemo.core.api.service.OfflineHandler;

public class OfflineRequestHandler implements HttpRequestHandler {

	Set<OfflineHandler> offlineSet;

	public OfflineRequestHandler setOfflineSet(Set<OfflineHandler> offlineSet) {
		this.offlineSet = offlineSet;
		return this;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (offlineSet != null) {
			for (OfflineHandler offline : offlineSet) {
				offline.offline();
			}
		}
		response.getWriter().print("{\"code\":\"200\",\"message\":\"success\"}");
	}

}

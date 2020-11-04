package com.jimistore.boot.nemo.gateway.server.core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("${nemo.gateway.dashbord.url:/}")
public class GatewayController {

	@GetMapping
	public String index() {
		return "index.html";
	}

}
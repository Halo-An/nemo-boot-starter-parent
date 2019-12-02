package com.jimistore.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import zipkin.server.EnableZipkinServer;

@EnableZipkinServer
@SpringBootApplication
public class NemoSleuthZipkinServerApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NemoSleuthZipkinServerApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(NemoSleuthZipkinServerApplication.class, args);
	}
}
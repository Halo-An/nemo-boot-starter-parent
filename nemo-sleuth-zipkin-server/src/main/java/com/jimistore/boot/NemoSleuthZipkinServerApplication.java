package com.jimistore.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

@SpringBootApplication
@EnableEurekaClient
@EnableZipkinStreamServer
public class NemoSleuthZipkinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NemoSleuthZipkinServerApplication.class, args);
	}
}

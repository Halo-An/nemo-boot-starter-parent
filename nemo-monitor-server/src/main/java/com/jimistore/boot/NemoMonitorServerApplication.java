package com.jimistore.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import de.codecentric.boot.admin.config.EnableAdminServer;

@EnableAdminServer
@EnableEurekaClient
@SpringBootApplication
public class NemoMonitorServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NemoMonitorServerApplication.class, args);
	}
}

package com.jimistore.boot.nemofusestarter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NemoFuseStarterApplication {

	public static void main(String[] args) {
		SpringApplication.run(NemoFuseStarterApplication.class, args);
		
		List<Object> list = new ArrayList<Object>();
		list.forEach((t)->{
			
		});
	}
}

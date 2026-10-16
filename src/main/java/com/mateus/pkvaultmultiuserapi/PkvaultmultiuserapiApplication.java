package com.mateus.pkvaultmultiuserapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PkvaultmultiuserapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PkvaultmultiuserapiApplication.class, args);
	}

}

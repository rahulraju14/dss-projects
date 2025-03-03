package com.example.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
//@EnableRetry
public class SpringSecurityApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApiApplication.class, args);
	}

}

package com.individual_s7.post_service;

import org.springframework.boot.SpringApplication;

public class TestPostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PostServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

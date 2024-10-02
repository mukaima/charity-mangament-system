package com.charity_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CharityManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(CharityManagementSystemApplication.class, args);
	}

}

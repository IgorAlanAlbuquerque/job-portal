package com.IgorAlan.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.IgorAlan.jobportal.entity")
public class JobportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobportalApplication.class, args);
	}

}

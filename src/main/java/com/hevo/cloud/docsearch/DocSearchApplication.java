package com.hevo.cloud.docsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hevo.cloud.docsearch")
public class DocSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocSearchApplication.class, args);
	}

}

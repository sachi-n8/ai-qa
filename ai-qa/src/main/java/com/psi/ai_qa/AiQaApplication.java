package com.psi.ai_qa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AiQaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiQaApplication.class, args);
	}

}

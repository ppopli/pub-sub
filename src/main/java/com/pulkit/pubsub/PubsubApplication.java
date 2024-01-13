package com.pulkit.pubsub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PubsubApplication {

	public static void main(String[] args) {
		SpringApplication.run(PubsubApplication.class, args);
	}

}

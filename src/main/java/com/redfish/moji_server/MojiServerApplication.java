package com.redfish.moji_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MojiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MojiServerApplication.class, args);
	}

}

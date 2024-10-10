package com.register.wowlibre;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.redis.repository.configuration.*;
import org.springframework.scheduling.annotation.*;

@EnableAsync
@SpringBootApplication
public class WowlibreApplication {

	public static void main(String[] args) {
		SpringApplication.run(WowlibreApplication.class, args);
	}

}

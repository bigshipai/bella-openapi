package com.ke.bella.openapi;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.ke.bella.openapi.job.queue.config.EnableJobQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMethodCache(basePackages = "com.ke.bella.openapi")
@EnableScheduling
@EnableJobQueue
public class OneTokenApplication {

	public static void main(String[] args) {
		SpringApplication.run(OneTokenApplication.class, args);
	}
}

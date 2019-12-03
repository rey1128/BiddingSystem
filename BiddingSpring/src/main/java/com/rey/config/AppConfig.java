package com.rey.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	@Value("${http.port:8080}")
	private int httpPort;
	
	@Bean
	public void showProp() {
		System.out.println("http_port: "+httpPort);
		
	}

}

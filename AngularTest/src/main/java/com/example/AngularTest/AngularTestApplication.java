package com.example.AngularTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.Arrays;

@SpringBootApplication
public class AngularTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AngularTestApplication.class, args);
	}

}

package com.example.wave;

import com.example.wave.other.SpotifyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SpotifyProperties.class)
public class WaveApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaveApplication.class, args);
	}
}

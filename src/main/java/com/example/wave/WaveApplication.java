package com.example.wave;

import com.example.wave.other.MessageCryptoProperties;
import com.example.wave.other.SpotifyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties({SpotifyProperties.class, MessageCryptoProperties.class})
@EnableCaching
public class WaveApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaveApplication.class, args);
	}
}

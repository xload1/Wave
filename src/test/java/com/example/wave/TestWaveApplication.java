package com.example.wave;

import org.springframework.boot.SpringApplication;

public class TestWaveApplication {

	public static void main(String[] args) {
		SpringApplication.from(WaveApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

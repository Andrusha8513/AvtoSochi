package ru.avtoAra.AvtoSochi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AvtoSochiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AvtoSochiApplication.class, args);
	}

}

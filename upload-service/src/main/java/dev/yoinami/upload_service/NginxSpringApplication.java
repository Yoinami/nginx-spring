package dev.yoinami.upload_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.yoinami.upload_service.util.MessageProducer;

@SpringBootApplication
public class NginxSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(NginxSpringApplication.class, args);
	}

	@Bean
    public CommandLineRunner sendDemoMessage(MessageProducer producer) {
		return args -> {
			while (true) {
			producer.sendMessage("Hello, RabbitMQ! at " + java.time.Instant.now().toString());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
			}
		};
    }

}

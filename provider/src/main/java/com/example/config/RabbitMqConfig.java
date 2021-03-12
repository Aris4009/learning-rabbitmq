package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
@PropertySource("classpath:rabbitmq.properties")
public class RabbitMqConfig {

	@Bean
	public ConnectionFactory connectionFactory(@Value("${q1.host}") final String host,
			@Value("${q1.port}") final int port) {
		final ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(host);
		connectionFactory.setPort(port);
		return connectionFactory;
	}
}

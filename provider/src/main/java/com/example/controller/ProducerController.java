package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@RestController
@RequestMapping("/api/producer")
public class ProducerController {

	private ConnectionFactory connectionFactory;

	public ProducerController(final ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@PostMapping("/send")
	public ResponseEntity<Object> send(@RequestBody final Object obj) {
		try (Connection connection = connectionFactory.newConnection(); Channel channel = connection.createChannel();) {

		} catch (final Exception e) {

		}
	}
}

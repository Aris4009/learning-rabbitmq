package com.example.controller.queue1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

@RestController
@RequestMapping("/api/mq")
public class Queue1ProducerController {

	private final Connection connection;

	private final String queueName;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public Queue1ProducerController(Connection connection, @Value("${q1.name}") String queueName) {
		this.connection = connection;
		this.queueName = queueName;
	}

	@PostMapping("/producer")
	public ResponseEntity<Object> send(@RequestBody final Object obj) {
		try (Channel channel = connection.createChannel()) {
			channel.queueDeclare(this.queueName, false, false, false, null);
			ObjectMapper objectMapper = new ObjectMapper();
			String msg = objectMapper.writeValueAsString(obj);
			channel.basicPublish("", queueName, null, msg.getBytes());
			log.info("producer msg:{}", msg);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}

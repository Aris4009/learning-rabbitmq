package com.example.consumer.queue1;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

@Service
public class Consumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Connection connection;

	private final String queueName;

	public Consumer(Connection connection, @Value("${q1.name}") String queueName) {
		this.connection = connection;
		this.queueName = queueName;
	}

	@PostConstruct
	public void consumer() {
		new Thread(() -> {
			try {
				Channel channel = connection.createChannel();
				channel.queueDeclare(queueName, false, false, false, null);
				DeliverCallback deliverCallback = (consumerTab, deliver) -> {
					String msg = new String(deliver.getBody());
					log.info("consumer tag:{}, msg:{}", consumerTab, msg);
				};
				channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
				});
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}).start();
	}
}

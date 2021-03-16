package com.example.work.queue;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	private static final Logger log = LoggerFactory.getLogger(Producer.class);

	private static final String QUEUE_NAME = "workQueue";

	public static void main(String[] args) {
		Connection connection = null;
		try {
			Properties properties = new Properties();
			properties.load(Producer.class.getClassLoader().getResourceAsStream("rabbitmq.properties"));
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(properties.getProperty("q1.host"));
			connectionFactory.setPort(Integer.parseInt(properties.getProperty("q1.port")));

			connection = connectionFactory.newConnection();
			Channel channel = connection.createChannel();
			log.info("producer already");
			producer(channel);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info("producer exit");
			if (connection != null) {
				try {
					connection.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private static void producer(Channel channel) throws IOException {
		try (Scanner scanner = new Scanner(System.in);) {
			StringBuilder builder = new StringBuilder(128);
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				if (s.isEmpty()) {
					if (builder.length() == 0) {
						continue;
					}

					for (int i = 0; i < builder.length(); i++) {
						channel.queueDeclare(QUEUE_NAME, false, false, false, null);
						String msg = String.valueOf(builder.charAt(i));
						channel.basicPublish("", QUEUE_NAME, null, msg.getBytes());
						log.info("producer:{}", msg);
					}
					builder.delete(0, builder.length());
					log.info("end input");
				} else if (s.equals("exit")) {
					return;
				} else {
					log.info("continue input");
					builder.append(s);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException | TimeoutException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
}

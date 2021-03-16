package com.example.pub.sub;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.work.queue.Producer;
import com.rabbitmq.client.*;

public class Consumer {

	private static final Logger log = LoggerFactory.getLogger(Consumer.class);

	private static final String ROUTING_KEY = "";

	private static final String EXCHANGE_NAME = "logs";

	private static final int CONSUMER_NUM = 3;

	public static void main(String[] args) {
		try {
			Properties properties = new Properties();
			properties.load(Producer.class.getClassLoader().getResourceAsStream("rabbitmq.properties"));
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(properties.getProperty("q1.host"));
			connectionFactory.setPort(Integer.parseInt(properties.getProperty("q1.port")));

			Connection connection = connectionFactory.newConnection();
			consumer(connection);

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					super.run();
					log.info("consumer exit");
					if (connection != null) {
						try {
							connection.close();
						} catch (IOException e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void consumer(Connection connection) {
		ExecutorService executorService = Executors.newFixedThreadPool(CONSUMER_NUM);
		for (int i = 0; i < CONSUMER_NUM; i++) {
			executorService.submit(new WorkerRunnable(connection));
		}
	}

	static class WorkerRunnable implements Runnable {

		private final Connection connection;

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		public WorkerRunnable(Connection connection) {
			this.connection = connection;
		}

		@Override
		public void run() {
			Channel channel = null;
			try {
				channel = connection.createChannel();
				channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
				String queueName = channel.queueDeclare().getQueue();
				channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
				DeliverCallback deliverCallback = (consumerTag, deliver) -> {
					String msg = new String(deliver.getBody());
					log.info("queueName:{},receive msg:{}", queueName, msg);
				};
				channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
				});
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}

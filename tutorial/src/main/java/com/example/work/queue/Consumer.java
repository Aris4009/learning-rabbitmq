package com.example.work.queue;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Consumer {

	private static final Logger log = LoggerFactory.getLogger(Consumer.class);

	private static final String QUEUE_NAME = "workQueue";

	private static final int CONSUMER_NUM = 20;

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
				channel.queueDeclare(QUEUE_NAME, false, false, false, null);
				DeliverCallback deliverCallback = (consumerTag, deliver) -> {
					String msg = new String(deliver.getBody());
					log.info("receive msg:{}", msg);
					try {
						Thread.sleep(5000L);
					} catch (InterruptedException exception) {
						log.error(exception.getMessage(), exception);
						Thread.currentThread().interrupt();
					}
					log.info("done msg:{}", msg);
				};
				channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
				});
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}

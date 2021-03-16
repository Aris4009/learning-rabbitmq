package com.example.pub.sub;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

	private static final Logger log = LoggerFactory.getLogger(Producer.class);

	private static final String ROUTING_KEY = "";

	private static final String EXCHANGE_NAME = "logs";

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
		// 创建exchange
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
		// 发布、订阅模型队列为临时队列，此方法生成一个队列，该队列具有非持久性、排他性、消费者断开时自动删除
		String queueName = channel.queueDeclare().getQueue();
		log.info("临时队列名称:{}", queueName);
		// 将exchange与队列绑定
		channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY);
		try {
			boolean flag = true;
			while (flag) {
				try {
					long r = System.currentTimeMillis();
					channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, String.valueOf(r).getBytes());
					log.info("producer:{}", r);
					Thread.sleep(500L);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					flag = false;
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

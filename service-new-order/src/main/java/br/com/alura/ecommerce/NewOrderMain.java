package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();) {
			try (KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();) {
				String email = Math.random() + "@email.com";
				for (int i = 0; i < 10; i++) {

					String orderId = UUID.randomUUID().toString();
					BigDecimal amount = new BigDecimal(Math.random() * 5000 + 1);

					Order order = new Order(orderId, amount, email);

					orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

					String emailCode = "Thanks! We're processing your order!";
					emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
				}
			}
		}
	}
}

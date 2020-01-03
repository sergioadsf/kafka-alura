package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class FraudDetectorService {

	public static void main(String[] args) throws Exception {

		FraudDetectorService fraudeService = new FraudDetectorService();
		try (KafkaService<Order> service = new KafkaService<>(FraudDetectorService.class.getSimpleName(),
				"ECOMMERCE_NEW_ORDER", fraudeService::parse, Order.class, new HashMap<>());) {
			service.run();
		}
	}
	
	private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

	private void parse(ConsumerRecord<String, Order> record) throws InterruptedException, ExecutionException {
		System.out.println("------------------------------------------");
		System.out.println("Processing new order, checking for fraud");
		System.out.println(record.key());

		Order order = record.value();
		System.out.println(record.partition());
		System.out.println(record.offset());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// ignoring
			e.printStackTrace();
		}

		if (isFraud(order)) {
			// pretending that the fraud happens when the amount is >= 4500
			System.out.println("Order is a fraud " + order);
			orderDispatcher.send("ECOMMERCE_ORDER_REJECTED", order.getUserId(), order);
		} else {
			System.out.println("Approved: " + order);
			orderDispatcher.send("ECOMMERCE_ORDER_APPROVED", order.getUserId(), order);
		}

	}

	private boolean isFraud(Order order) {
		return order.getAmount().compareTo(new BigDecimal("4500")) >= 0;
	}

}

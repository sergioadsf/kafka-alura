package br.com.alura.ecommerce;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

public class LogService {

	public static void main(String[] args) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

		LogService logService = new LogService();
		try (KafkaService<String> service = new KafkaService<>(LogService.class.getSimpleName(),
				Pattern.compile("ECOMMERCE.*"), logService::parse, String.class, map);) {
			service.run();
		}
	}

	private void parse(ConsumerRecord<String, String> record) {
		System.out.println("------------------------------------------");
		System.out.println("LOG: " + record.topic());
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
	}

}

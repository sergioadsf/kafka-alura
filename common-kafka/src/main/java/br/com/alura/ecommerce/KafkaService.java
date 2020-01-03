package br.com.alura.ecommerce;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

class KafkaService<T> implements Closeable {

	private final KafkaConsumer<String, T> consumer;
	private final ConsumerFunction<T> parse;
	
	private KafkaService(String groupId, ConsumerFunction<T> parse, Class<T> type, Map<String, String> map) {
		this.parse = parse;
		this.consumer = new KafkaConsumer<>(getProperties(groupId, type, map));
	}

	KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> map) {
		this(groupId, parse, type, map);
		consumer.subscribe(Collections.singletonList(topic));
	}

	KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> map) {
		this(groupId, parse, type, map);
		consumer.subscribe(topic);
	}

	void run() throws Exception {
		while (true) {
			ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(100));
			if (!records.isEmpty()) {
				System.out.println("Encontrei " + records.count() + " registros");
				for (ConsumerRecord<String, T> record : records) {
					try {
						parse.consume(record);
					} catch (InterruptedException e) {
						// so far, just logging the exception for this message
						e.printStackTrace();
					} catch (ExecutionException e) {
						// so far, just logging the exception for this message
						e.printStackTrace();
					}
				}
			}
		}
	}

	private Properties getProperties(String groupId, Class<T> type, Map<String, String> map) {
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
		properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
		properties.putAll(map);
		
		return properties;
	}

	@Override
	public void close() {
		this.consumer.close();
	}

}

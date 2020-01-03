package br.com.alura.ecommerce;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonDeserializer<T> implements Deserializer<T> {

	public static final String TYPE_CONFIG = "br.com.conectasol.type_config";
	private final Gson gson = new GsonBuilder().create();
	private Class<T> classType;

	@SuppressWarnings("unchecked")
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		String typeName = String.valueOf(configs.get(TYPE_CONFIG));
		try {
			this.classType = (Class<T>) Class.forName(typeName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Type for deserialization does not exists in the classpath!", e);
		}
	}

	@Override
	public T deserialize(String topic, byte[] bytes) {

		return gson.fromJson(new String(bytes), classType);
	}

}

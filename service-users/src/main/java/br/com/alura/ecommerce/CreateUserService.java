package br.com.alura.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class CreateUserService {

	private final Connection connection;

	public CreateUserService() throws SQLException {
		String url = "jdbc:sqlite:service-users/target/users_database.db";
		this.connection = DriverManager.getConnection(url);
		try {
			this.connection.createStatement()
					.execute("create table Users(" + "uuid varchar(200) primary key," + "email varchar(200))");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		CreateUserService userService = new CreateUserService();
		try (KafkaService<Order> service = new KafkaService<>(CreateUserService.class.getSimpleName(),
				"ECOMMERCE_NEW_ORDER", userService::parse, Order.class, new HashMap<>());) {

			service.run();
		}
	}

	private void parse(ConsumerRecord<String, Order> record)
			throws InterruptedException, ExecutionException, SQLException {
		System.out.println("------------------------------------------");
		System.out.println("Processing new order, checking for user");
		System.out.println(record.key());

		Order order = record.value();
		System.out.println(order);
		if (this.isNewUser(order.getEmail())) {
			this.insertNewUser(order.getEmail());
		}
	}

	private void insertNewUser(String email) throws SQLException {
		PreparedStatement ps = this.connection.prepareStatement("insert into Users(uuid, email) values (?, ?)");
		ps.setString(1, UUID.randomUUID().toString());
		ps.setString(2, email);
		ps.execute();

		System.out.println("Usuário uuid e " + email + "adicionado!");
	}

	private boolean isNewUser(String email) throws SQLException {
		PreparedStatement ps = this.connection.prepareStatement("select uuid from Users "
				+ "where email = ? limit 1");
		ps.setString(1, email);
		ResultSet rs = ps.executeQuery();
		return rs.next();
	}

}

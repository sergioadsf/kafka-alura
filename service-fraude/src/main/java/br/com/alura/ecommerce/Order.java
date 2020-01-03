package br.com.alura.ecommerce;

import java.math.BigDecimal;

public class Order {

	private final String userId;
	private final String orderId;
	private final BigDecimal amount;

	public Order(String userId, String orderId, BigDecimal amount) {
		super();
		this.userId = userId;
		this.orderId = orderId;
		this.amount = amount;
	}

	public String getUserId() {
		return userId;
	}

	public String getOrderId() {
		return orderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

}

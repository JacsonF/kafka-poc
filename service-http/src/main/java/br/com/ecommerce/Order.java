package br.com.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String orderId, email;
    private final BigDecimal value;

    public Order(String orderId, BigDecimal value, String email) {
        this.orderId = orderId;
        this.value = value;
        this.email = email;
    }
}

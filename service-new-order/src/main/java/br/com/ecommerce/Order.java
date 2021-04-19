package br.com.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String userId,orderId;
    private final BigDecimal value;

    public Order(String userId, String orderId, BigDecimal value) {
        this.userId = userId;
        this.orderId = orderId;
        this.value = value;
    }
}

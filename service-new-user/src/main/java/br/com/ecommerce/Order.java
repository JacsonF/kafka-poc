package br.com.ecommerce;

import java.math.BigDecimal;

public class Order {
    private final String userId,orderId;
    private final BigDecimal value;
    private final String email;

    public Order(String userId, String orderId, BigDecimal value, String email) {
        this.userId = userId;
        this.orderId = orderId;
        this.value = value;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}

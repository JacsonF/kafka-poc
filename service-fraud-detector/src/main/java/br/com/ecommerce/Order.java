package br.com.ecommerce;

import org.apache.kafka.common.protocol.types.Field;

import java.math.BigDecimal;

public class Order {
    private final String orderId, email;
    private final BigDecimal value;

    public Order(String orderId, BigDecimal value, String email) {
        this.orderId = orderId;
        this.value = value;
        this.email = email;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", email='" + email + '\'' +
                ", value=" + value +
                '}';
    }
}

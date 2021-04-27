package br.com.ecommerce;

import java.util.UUID;

public class CorrelationId {
    private final String id;

    public CorrelationId(String title) {
        this.id = title+"("+ UUID.randomUUID().toString()+")";
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CorrelationId{" +
                "id='" + id + '\'' +
                '}';
    }

    public CorrelationId continueWith(String title) {
        return new CorrelationId(id + "-" + title);
    }
}

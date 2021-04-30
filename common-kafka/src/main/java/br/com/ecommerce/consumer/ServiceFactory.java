package br.com.ecommerce.consumer;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}

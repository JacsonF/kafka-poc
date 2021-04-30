package br.com.ecommerce;

public interface ServiceFactory<T> {
    ConsumerSerivce<T> create();
}

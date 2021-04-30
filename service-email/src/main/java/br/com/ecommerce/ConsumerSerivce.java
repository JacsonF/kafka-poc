package br.com.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerSerivce<T>{
    void parse(ConsumerRecord<String,Message<T>>record);
    String getTopic();
    String getConsumerGroup();

}

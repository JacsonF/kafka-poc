package br.com.ecommerce.consumer;

import br.com.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface ConsumerService<T>{
    void parse(ConsumerRecord<String, Message<T>>record) throws Exception;
    String getTopic();
    String getConsumerGroup();

}

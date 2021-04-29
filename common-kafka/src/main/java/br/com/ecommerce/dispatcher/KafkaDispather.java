package br.com.ecommerce.dispatcher;

import br.com.ecommerce.CorrelationId;
import br.com.ecommerce.Message;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispather<T> implements Closeable {
    private final KafkaProducer<String, Message<T>> producer;


    public KafkaDispather(){
        this.producer = new KafkaProducer<>(properties());
    }
    private static Properties properties() {
        var  properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9091");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG,"all");
        return properties;
    }


    public void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
        Future<RecordMetadata> future = sendAsync(topic, key, id, payload);
        future.get();
    }

    public Future<RecordMetadata> sendAsync(String topic, String key, CorrelationId id, T payload) {
        var value = new Message<T>(id.continueWith("_"+topic), payload);
        var record = new ProducerRecord<>(topic,
                key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            } else {
                System.out.println("Enviado com sucesso" + " DATA TOPIC: " + data.topic() + " /OFFSET: " + data.offset() + " / DATA PARTITION: " + data.partition() + " DATA TIMESTAMP" + data.timestamp());
            }
        };
        var future = producer.send(record, callback);
        return future;
    }

    @Override
    public void close(){
        producer.close();
    }
}

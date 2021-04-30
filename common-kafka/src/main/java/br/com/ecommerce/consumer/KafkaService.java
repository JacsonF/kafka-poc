package br.com.ecommerce.consumer;

import br.com.ecommerce.Message;
import br.com.ecommerce.dispatcher.GsonSerializer;
import br.com.ecommerce.dispatcher.KafkaDispather;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse,  Map<String,String> extraProperties) {
        this(groupId,parse,extraProperties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse,Map<String,String> extraProperties) {
        this(groupId,parse,extraProperties);
        consumer.subscribe(topic);
    }

    private KafkaService(String groupId, ConsumerFunction<T> parse,Map<String,String> extraProperties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(properties(groupId,extraProperties));
    }


    public void run() throws ExecutionException, InterruptedException {
        try(var deadLetter = new KafkaDispather<>()) {
            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));
                if (!records.isEmpty()) {
                    System.out.println("Encontrei: " + records.count() + " registros");
                    for (var record : records) {
                        try {
                            parse.consume(record);
                        } catch (Exception e) {
                            var message = record.value();
                            deadLetter.send("ECOMMERCE_DEADLETTER", message.getId().toString(), message.getId().continueWith("DeadLetter"), new GsonSerializer().serialize("", message));
                            // ONLY CATCHES EXCEPTION BECAUSE NO MATTER WHICH EXCEPTION
                            // I WANT TO RECOVER AND PARSE TO NEXT ONE
                            // SO FAR JUST LOGGING THE EXEPTION FOR THIS MESSAGE
                            System.out.println("Error: ");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    private Properties properties(String groupId, Map<String, String> overrideProperties){
        var  properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9091");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        //Eu preciso passar um grupo para o kafka para ele poder distribuir para mais serviços que tenham esse mesmo grupo
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.putAll(overrideProperties);
        return properties;
    }

    @Override
    public void close() {
    }
}

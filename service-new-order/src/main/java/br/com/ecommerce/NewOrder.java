package  br.com.ecommerce;

import br.com.ecommerce.dispatcher.KafkaDispather;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrder {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var dispatcher = new KafkaDispather<Order>()){
            try(var emailDispatcher = new KafkaDispather<Email>()){
                var email = Math.random() + "@gmail.com";
                for ( var i =0; i<10; i++) {

                    var orderId =UUID.randomUUID().toString();
                    var amout = new BigDecimal(Math.random()* 500+1);


                    var order = new Order(orderId,amout,email);
                    dispatcher.send("ECOMMERCE_NEW_ORDER" ,email, new CorrelationId(NewOrder.class.getSimpleName()),order);
                }
            }
        }
    }
}

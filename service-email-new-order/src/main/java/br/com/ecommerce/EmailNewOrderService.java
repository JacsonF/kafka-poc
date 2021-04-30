package br.com.ecommerce;

import br.com.ecommerce.consumer.ConsumerService;
import br.com.ecommerce.consumer.KafkaService;
import br.com.ecommerce.consumer.ServiceRunner;
import br.com.ecommerce.dispatcher.KafkaDispather;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService  implements ConsumerService<Order> {
    private static final int THREAD = 1;
    private final KafkaDispather<Email> emailDispather = new KafkaDispather<>();

    public static void main(String[] args)  throws ExecutionException, InterruptedException{
            new ServiceRunner(EmailNewOrderService::new).start(THREAD);
    }
    public void parse(ConsumerRecord<String, Message<Order>>record) throws ExecutionException, InterruptedException {
        System.out.println("------------------------------------");
        System.out.println("Processing order, preparing email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.offset());
        System.out.println("------------------------------------");
        var message = record.value();
        Order order = message.getPayload();
        var correlationId = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        var subject = "jackson@kafka.com";
        var body = "Tank you for your order! We are processing your order";
        Email emailCode = new Email(subject,body);
        emailDispather.send("ECOMMERCE_SEND_EMAIL",
                order.getEmail(),
                correlationId,
                emailCode);
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}

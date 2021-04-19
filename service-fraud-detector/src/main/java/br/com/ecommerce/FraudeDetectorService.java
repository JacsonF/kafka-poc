package br.com.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FraudeDetectorService {
    private final KafkaDispather<Order> dispather = new KafkaDispather<>();

    public static void main(String[] args) {
        var fraudeDetectectorService = new FraudeDetectorService();
        try(var service = new KafkaService<Order>(FraudeDetectorService.class.getSimpleName(),"ECOMMERCE_NEW_ORDER"
                ,fraudeDetectectorService::parse, Order.class, Map.of())){
            service.run();
        }
    }
    private void parse(ConsumerRecord<String,Order>record) throws ExecutionException, InterruptedException {
        System.out.println("------------------------------------");
        System.out.println("Processing order, cheking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.offset());
        System.out.println("------------------------------------");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        var order = record.value();
        if (isFraud(order)){
            // pretending that the fraud happens when the amout is >=4500
            System.out.println("Order is a fraud!!!");
            dispather.send("ECOMMERCE_ORDER_REJECTED",order.getUserId(),order);
        }else{
            System.out.println("Aproved :"+ order);
            dispather.send("ECOMMERCE_ORDER_APPROVED",order.getUserId(),order);
        }
        System.out.println("SUCESSO");
    }

    private boolean isFraud(Order order) {
        return order.getValue().compareTo(new BigDecimal("4500")) >= 0;
    }
}

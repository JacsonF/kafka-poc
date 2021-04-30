package br.com.ecommerce;

import br.com.ecommerce.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailService implements ConsumerSerivce<Email>{
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new ServiceProvider().run(EmailService::new);
    }
    public String getTopic(){
        return "ECOMMERCE_SEND_EMAIL";
    }
    public String getConsumerGroup(){
        return EmailService.class.getSimpleName();
    }
    public void parse(ConsumerRecord<String,Message<Email>> record){
        var message = record.value();
        System.out.println("------------------------------------");
        System.out.println("Send email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.offset());
        System.out.println("Email: ");
        System.out.println(message.getPayload().toString());
        System.out.println("------------------------------------");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        System.out.println("EMAIL ENVIADO");
    }
}

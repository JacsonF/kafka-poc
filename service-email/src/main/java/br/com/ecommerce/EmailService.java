package br.com.ecommerce;

import br.com.ecommerce.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmailService {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        var emailService = new EmailService();
        try(KafkaService service = new KafkaService(EmailService.class.getSimpleName(), "ECOMMERCE_SEND_EMAIL", emailService::parse, Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String,Message<Email>> record){
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

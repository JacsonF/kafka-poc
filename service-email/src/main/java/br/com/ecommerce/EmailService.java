package br.com.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public class EmailService {
    public static void main(String[] args) {
        var emailService = new EmailService();
        try(KafkaService service = new KafkaService(EmailService.class.getSimpleName(), "ECOMMERCE_SEND_EMAIL", emailService::parse,Email.class, Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String,Email> record){
        System.out.println("------------------------------------");
        System.out.println("Send email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.offset());
        System.out.println("------------------------------------");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        System.out.println("EMAIL ENVIADO");
    }
}
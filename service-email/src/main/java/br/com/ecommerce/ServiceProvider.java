package br.com.ecommerce;

import br.com.ecommerce.consumer.KafkaService;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> {
    public <T> void run(ServiceFactory<T> factory) throws ExecutionException, InterruptedException {

        var serviceEmail = factory.create();

        try(KafkaService service = new KafkaService(serviceEmail.getConsumerGroup(),
                serviceEmail.getTopic(),
                serviceEmail::parse,
                Map.of())){
            service.run();
        }
    }
}

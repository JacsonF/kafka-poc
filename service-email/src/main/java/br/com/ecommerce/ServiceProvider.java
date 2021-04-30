package br.com.ecommerce;

import br.com.ecommerce.consumer.KafkaService;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void> {
    private final ServiceFactory<T> factory;
    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    public Void call() throws ExecutionException, InterruptedException {
        var myService = factory.create();

        try(KafkaService service = new KafkaService(myService.getConsumerGroup(),
                myService.getTopic(),
                myService::parse,
                Map.of())){
            service.run();
        }
        return null;
    }
}

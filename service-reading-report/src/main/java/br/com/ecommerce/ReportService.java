package br.com.ecommerce;

import br.com.ecommerce.consumer.ConsumerService;
import br.com.ecommerce.consumer.KafkaService;
import br.com.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ReportService implements ConsumerService<User> {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();
    private static final int THREADS = 1;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(ReportService::new).start(THREADS);
    }
    public void parse(ConsumerRecord<String,Message<User>>record) throws IOException {
        var message = record.value();
        var user = message.getPayload();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);


        System.out.println("Correlation Id------------------------------------> "+message.getId());
        System.out.println("Processing report for "+ message);
        System.out.println("Processing report for "+ user.getUuid());
        IO.append(target, "Create for: "+ user.getUuid());
        System.out.println("File created"+ target.getAbsolutePath());
        System.out.println("------------------------------------");
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REPORT";
    }

    @Override
    public String getConsumerGroup() {
        return ReportService.class.getSimpleName();
    }
}

package br.com.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class ReportService {

    private static final Path SOURCE = new File("src/main/resources/report.txt").toPath();
    public static void main(String[] args) {
        var reportService = new ReportService();
        try(var service = new KafkaService<>(ReportService.class.getSimpleName(),"USER_GENERATE_READING_REPORT"
                ,reportService::parse, User.class, Map.of())){
            service.run();
        }
    }
    private void parse(ConsumerRecord<String,Message<User>>record) throws IOException {
        System.out.println("------------------------------------");

        var message = record.value();
        var user = message.getPayload();
        var target = new File(user.getReportPath());
        System.out.println("Processing report for "+ user.getUuid());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Create for: "+ user.getUuid());
        System.out.println("File created"+ target.getAbsolutePath());
        System.out.println("------------------------------------");
    }
}

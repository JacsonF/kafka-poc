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
    private void parse(ConsumerRecord<String,User>record) throws IOException {
        System.out.println("------------------------------------");
        System.out.println("Processing report for "+ record.value().getUuid());

        var user = record.value();
        var target = new File(user.getReportPath());
        IO.copyTo(SOURCE, target);
        IO.append(target, "Create for: "+ user.getUuid());
        System.out.println("File created"+ target.getAbsolutePath());
        System.out.println("------------------------------------");
    }
}

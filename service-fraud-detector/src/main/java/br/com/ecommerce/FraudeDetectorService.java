package br.com.ecommerce;

import br.com.ecommerce.consumer.ConsumerService;
import br.com.ecommerce.consumer.ServiceRunner;
import br.com.ecommerce.dispatcher.KafkaDispather;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FraudeDetectorService implements ConsumerService<Order> {
    private static final int THREAD = 1;
    private final KafkaDispather<Order> dispather = new KafkaDispather<>();
    private final LocalDatabase dataBase;

    public FraudeDetectorService() throws SQLException {
        this.dataBase = new LocalDatabase("fraud_database");
        this.dataBase.createIfNoteExists("create table Orders(" +
                " uuid varchar(200) primary key, " +
                " is_fraud boolean )");
    }

    public static void main(String[] args){
        new ServiceRunner(FraudeDetectorService::new).start(THREAD);
    }

    public void parse(ConsumerRecord<String,Message<Order>>record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("------------------------------------");
        System.out.println("Processing orders, cheking for fraud");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.offset());
        System.out.println("------------------------------------");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            System.out.println(ex);
        }
        var message = record.value();
        var order = message.getPayload();
        if (wasProcessed(order)){
            System.out.println("order: "+order.getOrderId()+" was alredy processed");
            return;
        }
        if (isFraud(order)){
            // pretending that the fraud happens when the amout is >=4500
            dataBase.update("insert into Orders(uuid, is_fraud) values(?,true)",order.getOrderId());
            System.out.println("Order is a fraud!!!");
            dispather.send("ECOMMERCE_ORDER_REJECTED",order.getEmail(), message.getId().continueWith(FraudeDetectorService.class.getSimpleName()),order);
        }else{
            dataBase.update("insert into Orders(uuid, is_fraud) values(?,false)",order.getOrderId());
            System.out.println("Aproved :"+ order);
            dispather.send("ECOMMERCE_ORDER_APPROVED",order.getEmail(),message.getId().continueWith(FraudeDetectorService.class.getSimpleName()),order);
        }
        System.out.println("SUCESSO");
    }

    private boolean wasProcessed(Order order) throws SQLException {
        System.out.println("minhaa order antes de estourar"+order.getOrderId());
        var results = dataBase.query("select uuid from Orders " +
                "where uuid = ? limit 1", order.getOrderId());
        return results.next();
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return FraudeDetectorService.class.getSimpleName();
    }

    private boolean isFraud(Order order) {
        return order.getValue().compareTo(new BigDecimal("4500")) >= 0;
    }
}

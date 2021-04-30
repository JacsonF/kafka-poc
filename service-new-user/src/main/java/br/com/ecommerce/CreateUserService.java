package br.com.ecommerce;

import br.com.ecommerce.consumer.ConsumerService;
import br.com.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {
    private static final int TRHEAD = 1;
    private final LocalDatabase dataBase;

    public CreateUserService() throws SQLException {
        this.dataBase = new LocalDatabase("users_database");
        this.dataBase.createIfNoteExists("create users(" +
                " uuid varchar(200) primary key, " +
                " email varchar(200) )");
    }

    public static void main(String[] args){
        new ServiceRunner<>(CreateUserService::new).start(TRHEAD);
    }

    public void parse(ConsumerRecord<String,Message<Order>> record) throws SQLException {

        System.out.println("------------------------------------");
        System.out.println("Processing new order, cheking for new user");
        System.out.println(record.value());

        var message = record.value();
        var order = message.getPayload();
        if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }else{
            System.out.println("Usuario  "+ order+" já foi adicionado");
            System.out.println("------------------------------------");
        }

    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        dataBase.update("insert into Users (uuid,email) " +
                "values(?,?)",UUID.randomUUID().toString(),
                email);
        System.out.println("Usuario uuid e "+email+" adicionado");
        System.out.println("------------------------------------");
    }

    private boolean isNewUser(String email) throws SQLException {
        var results =dataBase.query("select uuid from Users " +
                "where email = ? limit 1", email);
        return  !results.next();

    }
}

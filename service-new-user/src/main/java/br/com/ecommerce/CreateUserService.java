package br.com.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {
    private final Connection connection;

    public CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users(" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        }catch (SQLException ex){
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try(var service = new KafkaService<>(CreateUserService.class.getSimpleName(),"ECOMMERCE_NEW_ORDER"
                ,createUserService::parse, Order.class, Map.of())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String,Order> record) throws ExecutionException, InterruptedException, SQLException {

        System.out.println("------------------------------------");
        System.out.println("Processing new order, cheking for new user");
        System.out.println(record.value());


        var order = record.value();
        if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }else{
            System.out.println("Usuario  "+ order+" já foi adicionado");
            System.out.println("------------------------------------");
        }

    }

    private void insertNewUser(String email) throws SQLException {
      var insert=  connection.prepareStatement("insert into Users (uuid,email) " +
                "values(?,?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2,email);

        insert.execute();
        System.out.println("Usuario uuid e "+email+" adicionado");
        System.out.println("------------------------------------");
    }

    private boolean isNewUser(String email) throws SQLException {
        var existis =connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        existis.setString(1,email);
        var results =existis.executeQuery();
        return  !results.next();

    }
}

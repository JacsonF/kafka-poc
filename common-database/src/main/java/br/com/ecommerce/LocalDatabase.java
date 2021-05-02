package br.com.ecommerce;

import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class LocalDatabase {
    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/"+name+".db";
        this.connection = DriverManager.getConnection(url);
    }
    //yes, this is way too generic. in the future, i will change to avoid injection;
    public void createIfNoteExists(String sql){
        try {
            connection.createStatement().execute(sql);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void update(String statement, String ... params) throws SQLException {
        PreparedStatement preparedStatement = prepare(statement, params);
        preparedStatement.execute();

    }
    public ResultSet query(String query, String... params) throws SQLException {
        return prepare(query, params).executeQuery();
    }
    private PreparedStatement prepare(String statement, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }

    public void close() throws IOException {
        try {
            connection.close();
        }catch (SQLException e){
            throw new IOException(e);
        }

    }
}

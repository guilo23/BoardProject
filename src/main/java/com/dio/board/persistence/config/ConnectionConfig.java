package com.dio.board.persistence.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionConfig {
    public static Connection getConnection()throws SQLException{
    var url = "jdbc:mysql://localhost:3306/board";
        var user = "root";
        var password = "admin";
        var connection = DriverManager.getConnection(url,user,password);
        connection.setAutoCommit(false);
        return connection;
    }

}

package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static Connection instance = null;


    private DatabaseConnection(String DbName) {
        String url = "jdbc:sqlite:" + DbName;
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    /**
     * Establishes a connection to a Database if a
     * connection instance doesn't already exist
     * @param DbName Name of Database to connect to
     */
    public static Connection getInstance(String DbName) {
        if (instance == null) {
            new DatabaseConnection(DbName);
        }
        return instance;
    }
}

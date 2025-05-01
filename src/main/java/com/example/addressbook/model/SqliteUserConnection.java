package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteUserConnection {
    private static Connection instance = null;
    private static SQLException lastException = null;

    private SqliteUserConnection(String url) {
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            lastException = sqlEx;
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteUserConnection("jdbc:sqlite:users.db");
        }
        return instance;
    }
    // testing method
    public static void resetConnection(String url) {
        instance = null;
        Object lastException = null;
        new SqliteUserConnection(url);
    }

    public static SQLException getLastException() {
        return lastException;
    }
}
package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteNoteConnection {
    private static Connection instance = null;
    private static SQLException lastException = null; // storing last exception for testing

    private SqliteNoteConnection(String url) {
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            lastException = sqlEx;
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteNoteConnection("jdbc:sqlite:users.db");
        }
        return instance;
    }

    // testing method
    public static void resetConnection(String url) {
        instance = null;
        lastException = null;
        new SqliteNoteConnection(url);
    }

    public static SQLException getLastException() {
        return lastException;
    }
}
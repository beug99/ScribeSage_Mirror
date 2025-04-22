package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/// could be combined with SqliteNoteConnection.java?

public class SqliteUserConnection {
    private static Connection instance = null;

    private SqliteUserConnection() {
        String url = "jdbc:sqlite:users.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteUserConnection();
        }
        return instance;
    }
}
package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/// could be combined with SqliteUserConnection.java?

public class SqliteNoteConnection {
    private static Connection instance = null;

    private SqliteNoteConnection() {
        String url = "jdbc:sqlite:notes.db";
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    public static Connection getInstance() {
        if (instance == null) {
            new SqliteNoteConnection();
        }
        return instance;
    }
}
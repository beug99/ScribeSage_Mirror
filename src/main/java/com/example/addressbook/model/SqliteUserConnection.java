package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class to create a single connection to SQLite Database - Implements singleton pattern
 */
public class SqliteUserConnection {
    private static Connection instance = null;
    private static SQLException lastException = null;

    /**
     * A constructor that initialises the connection to the database.
     * @param url The database url.
     */
    private SqliteUserConnection(String url) {
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            lastException = sqlEx;
            System.err.println(sqlEx);
        }
    }

    /**
    * A method that provides global access to the instance of the database connection.
     * @return A handle to the instance of the database connection.
     */
    public static Connection getInstance() {
        if (instance == null) {
            new SqliteUserConnection("jdbc:sqlite:users.db");
        }
        return instance;
    }

    /**
     * A testing method that resets the connection to the database.
     * @param url The url connection to be reset.
     */
    public static void resetConnection(String url) {
        instance = null;
        Object lastException = null;
        new SqliteUserConnection(url);
    }

    /**
     * A testing method that returns the latest SQL Exception.
     * @return The exception.
     */
    public static SQLException getLastException() {
        lastException = new SQLException();
        return lastException;
    }
}
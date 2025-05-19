package com.example.addressbook.model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Class to create a single connection to SQLite Note Database - Implements singleton pattern.
 * Assists
 */
public class SqliteNoteConnection {
    private static Connection instance = null;
    private static SQLException lastException = null; // storing last exception for testing

    /**
     * A constructor that initialises the connection to the database.
     * @param DbName The name of the database.
     */
    private SqliteNoteConnection(String DbName) {
        String url = "jdbc:sqlite:" + DbName;
        try {
            instance = DriverManager.getConnection(url);
        } catch (SQLException sqlEx) {
            System.err.println(sqlEx);
        }
    }

    /**
     * A method that provides global access to the instance of the database connection.
     * @param DbName The name of the database.
     * @return A handle to the instance of the database connection.
     */
    public static Connection getInstance(String DbName) {
        if (instance == null) {
            new SqliteNoteConnection(DbName);
        }
        return instance;
    }


    /**
     * A testing method that resets the connection to the database.
     * @param url The url connection to be reset.
     */
    public static void resetConnection(String url) {
        instance = null;
        lastException = null;
        new SqliteNoteConnection(url);
    }

    /**
     * A testing method that returns the latest SQL Exception.
      * @return The exception.
     */
    public static SQLException getLastException() {
        lastException = new SQLException();
        return lastException;
    }

    /**
     * A testing method that closes the connection to the database.
     * @param url The url of the database which connection will be closed.
     */
    public static void closeConnection(String url){
        SqliteNoteConnection.closeConnection("notes.db");
    }
}
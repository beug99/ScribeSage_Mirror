package com.example.addressbook.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserDAO implements IUserDAO {
    private static SqliteNoteConnection SqliteConnection;
    private Connection connection;

    public SqliteUserDAO() {
        connection = SqliteUserConnection.getInstance();
        createTable();
    }

    private void createTable() {
        // Create table if not exists
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "firstName VARCHAR NOT NULL,"
                    + "lastName VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL,"
                    + "password VARCHAR NOT NULL,"
                    + "userId INTEGER AUTOINCREMENT"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (firstName, lastName, email, password, userId) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.setInt(5, user.getUserId());
            statement.executeUpdate();
            // Set the id of the new contact
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setUserId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET firstName = ?, lastName = ?, password = ?, email = ?, userId = ? WHERE id = ?");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getEmail());
            statement.setInt(5, user.getUserId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM contacts WHERE id = ?");
            statement.setInt(1, user.getUserId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Integer userId = resultSet.getInt("userId");
                User user = new User(firstName, lastName, email, password, userId);
                user.setUserId(id);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM users";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Integer userId = resultSet.getInt("userId");

                User user = new User(firstName, lastName, email, password, userId);
                user.setUserId(id);
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static boolean authenticateUser(String email, String password) {
        boolean isAuthenticated = false;
        try {
            // match email and password parameters with userbase
            PreparedStatement statement = SqliteUserConnection.getInstance().prepareStatement(
                    "SELECT * FROM users WHERE email = ? AND password = ?");

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            isAuthenticated = resultSet.next();
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return isAuthenticated;
    }

    public static boolean updateEmail(String currentEmail, String password, String newEmail) {
        boolean isUpdated = false;

        try {
            // Check if the current email and password match
            PreparedStatement checkStmt = SqliteUserConnection.getInstance().prepareStatement(
                    "SELECT * FROM users WHERE email = ? AND password = ?");
            checkStmt.setString(1, currentEmail);
            checkStmt.setString(2, password);

            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                // If valid, update the email
                PreparedStatement updateStmt = SqliteUserConnection.getInstance().prepareStatement(
                        "UPDATE users SET email = ? WHERE email = ?");
                updateStmt.setString(1, newEmail);
                updateStmt.setString(2, currentEmail);
                int rowsAffected = updateStmt.executeUpdate();
                isUpdated = (rowsAffected > 0);

                updateStmt.close();
            }

            resultSet.close();
            checkStmt.close();

        } catch (SQLException e) {
            System.err.println("Email update error: " + e.getMessage());
        }

        return isUpdated;
    }



    public static boolean updatePassword(String email, String oldPassword, String newPassword) {
        boolean isUpdated = false;

        try {
            // check if the old password matches input
            PreparedStatement checkStmt = SqliteUserConnection.getInstance().prepareStatement(
                    "SELECT * FROM users WHERE email = ? AND password = ?");
            checkStmt.setString(1, email);
            checkStmt.setString(2, oldPassword);

            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                // if password is correct, proceed to update
                PreparedStatement updateStmt = SqliteUserConnection.getInstance().prepareStatement(
                        "UPDATE users SET password = ? WHERE email = ?");
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, email);
                int rowsAffected = updateStmt.executeUpdate();
                isUpdated = (rowsAffected > 0);

                updateStmt.close();
            }

            resultSet.close();
            checkStmt.close();

        } catch (SQLException e) {
            System.err.println("Password update error: " + e.getMessage());
        }

        return isUpdated;
    }


    public static User getUserByEmail(String email) {
        try {
            Connection conn = SqliteUserConnection.getInstance();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("id")
                );
                user.setUserId(rs.getInt("id"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

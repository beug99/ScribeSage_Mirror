package com.example.addressbook.model;

import com.example.addressbook.helper.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This DAO class will handle the CRUD operations with the SQlite User database. This class implements
 * the {@link IUserDAO} interface. It handles the operations for user creation, viewing, editing and deletion.
 */
public class SqliteUserDAO implements IUserDAO {
    private static SqliteNoteConnection SqliteConnection;
    private Connection connection;

    /**
     * Constructs an instance of the SqliteUserDAO and calls the {@link #createTable()} method.
     */
    public SqliteUserDAO() {
        connection = SqliteUserConnection.getInstance();
        createTable();
    }

    /**
     * A method that creates an instance of a table to store the contents that will be saved to the database.
     */
    private void createTable() {
        // Create table if not exists
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "firstName VARCHAR NOT NULL,"
                    + "lastName VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL,"
                    + "password VARCHAR NOT NULL"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that adds a user to the User database.
     * @param user The User to add to the database.
     */
    @Override
    public void addUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (firstName, lastName, email, password) VALUES (?, ?, ?, ?)");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPassword());
            statement.executeUpdate();
            // Set the id of the new contact
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method That will update the database with updated information on the selected user.
     * @param user The User to update.
     */
    @Override
    public void updateUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE contacts SET firstName = ?, lastName = ?, phone = ?, email = ? WHERE id = ?");
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getEmail());
            statement.setInt(5, user.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that will delete the selected user from the database.
     * @param user The User to delete.
     */
    @Override
    public void deleteUser(User user) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM contacts WHERE id = ?");
            statement.setInt(1, user.getId());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that will retrieve the data on a selected user.
     * @param id The id of the User to retrieve.
     * @return The users first and last name, email and password.
     */
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
                User user = new User(firstName, lastName, email, password);
                user.setId(id);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method that will get a list of current users from the database.
     * @return A list of existing users including their first and last name, email and passwords.
     */
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

                User user = new User(firstName, lastName, email, password);
                user.setId(id);
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * A method that will authenticate if a user exists and if their password patches their email.
     * This is called when a user attempts to log in to their account.
     * @param email The email address the user enters.
     * @param password The password the user enters.
     * @return True if the user account details match, False if they don't match or don't exist.
     */
    public static boolean authenticateUser(String email, String password) {
        boolean isAuthenticated = false;
        try {
            // match email and password parameters with userbase
            PreparedStatement statement = SqliteUserConnection.getInstance().prepareStatement(
                    "SELECT password FROM users WHERE email = ?");

            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");
                // verifying password with password4j
                isAuthenticated = PasswordHasher.verifyPassword(password, storedHash);
            }
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return isAuthenticated;
    }

    /**
     * A method that updates the password of an existing user. They must enter their email and current
     * password to authenticate themselves before being permitted to chane their password. The method
     * uses {@link #isValidPassword(String)} to check if the password is valid before updating it.
     * @param email The email address of the user.
     * @param oldPassword The current password of the user.
     * @param newPassword The new password of the user.
     * @return True if the password update is successful, false if it fails to update.
     */
    public static boolean updatePassword(String email, String oldPassword, String newPassword) {
        boolean isUpdated = false;

        try {
            // get password hash
            PreparedStatement getStmt = SqliteUserConnection.getInstance().prepareStatement(
                    "SELECT password FROM users WHERE email = ?");
            getStmt.setString(1, email);
            ResultSet resultSet = getStmt.executeQuery();

            if(isValidPassword(newPassword)) {

                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");

                    // verifying old password using password4j
                    if (PasswordHasher.verifyPassword(oldPassword, storedHash)) {
                        String newHashedPassword = PasswordHasher.hashPassword(newPassword);

                        // updated database
                        PreparedStatement updateStmt = SqliteUserConnection.getInstance().prepareStatement(
                                "UPDATE users SET password = ? WHERE email = ?");
                        updateStmt.setString(1, newHashedPassword);
                        updateStmt.setString(2, email);
                        int rowsAffected = updateStmt.executeUpdate();
                        isUpdated = (rowsAffected > 0);

                        updateStmt.close();
                    }
                }
                resultSet.close();
                getStmt.close();
            }
        }
        catch (SQLException e) {
            System.err.println("Password update error: " + e.getMessage());
        }
        return isUpdated;
    }

    /**
     * A method that updates the email of an existing user. They must enter their email to
     * authenticate themselves before being permitted to chane their email. The method
     * uses {@link #isValidEmail(String)} to check if the email is valid before updating it.
     * @param currentEmail Users current email address.
     * @param password Users password.
     * @param newEmail The new email address.
     * @return True if the email is successfully updated, false it this fails.
     */
    public static boolean updateEmail(String currentEmail, String password, String newEmail) {
        boolean isUpdated = false;
        if(isValidEmail(newEmail)) {
            try {
                // Check if the current email exists
                PreparedStatement getStmt = SqliteUserConnection.getInstance().prepareStatement(
                        "SELECT password FROM users WHERE email = ?"
                );
                getStmt.setString(1, currentEmail); // placeholder

                ResultSet resultSet = getStmt.executeQuery();
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");

                    // verify password with password4j
                    if (PasswordHasher.verifyPassword(password, storedHash)) {
                        // If valid, update the email
                        PreparedStatement updateStmt = SqliteUserConnection.getInstance().prepareStatement(
                                "UPDATE users SET email = ? WHERE email = ?"
                        );
                        updateStmt.setString(1, newEmail);
                        updateStmt.setString(2, currentEmail);
                        int rowsAffected = updateStmt.executeUpdate();
                        isUpdated = (rowsAffected > 0);

                        updateStmt.close();
                    }
                }

                resultSet.close();
                getStmt.close();

            } catch (SQLException e) {
                System.err.println("Email update error: " + e.getMessage());
            }
        }
        return isUpdated;
    }

    /**
     *
     * @param email The email address to be searched.
     * @return A user if one exists with this email, else returns null.
     */
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
                        rs.getString("password")
                );
                user.setId(rs.getInt("id"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method that analyses the email entered by the user. If the email is written in the correct
     * syntax it is valid, else it is invalid.
     * @param email The email entered by the user.
     * @return True if valid, False if invalid.
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    /**
     * A method that analyses the password entered by the user. If the password is written in the
     * correct syntax it is valid, else it is invalid.
     * @param password The password entered by the user.
     * @return True if valid, false if invalid.
     */
    public static boolean isValidPassword(String password){
        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

}

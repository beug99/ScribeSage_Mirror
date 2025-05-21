package com.example.addressbook.model;

/**
 * A model class that represents a User with an ID, a first name, last name, email address and password.
 */
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    /**
     * Constructs a new User with the specified first name, last name, email address and password.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param email The email address of the user.
     * @param password The password of the user.
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * This is the getter method for the user ID.
     * @return The ID of the user.
     */
    public int getId() {
        return id;
    }

    /**
     * This is the setter method for the user ID.
     * @param id The ID of the user.
     */
    public void setId (int id) {
        this.id = id;
    }

    /**
     * This is the getter method for the first name of the user.
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This is the setter method for the first name of the user. The parameter is set through user input.
     * @param firstName The first name of the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * This is the getter method for the last name of the user.
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This is the setter method for the last name of the user. The parameter is set through user input.
     * @param lastName The last name of the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * This is the getter method for the email address of the user.
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * This is the setter method for the email address of the user. The parameter is set through user input.
     * @param email The email address of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * This method returns the full name of the user by merging the users first name and last name.
     * @return The users full name - both the first name and last name as one string.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * This is the getter method for the password of the user.
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * This is the setter method for the password of the user. The parameter is set through user input.
     * @param password The password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

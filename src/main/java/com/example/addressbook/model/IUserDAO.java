package com.example.addressbook.model;

import java.util.List;

public interface IUserDAO {
    /**
     * Adds a new User to the database.
     * @param user The User to add.
     */
    public void addUser(User user);
    /**
     * Updates an existing User in the database.
     * @param user The User to update.
     */
    public void updateUser(User user);
    /**
     * Deletes a User from the database.
     * @param user The User to delete.
     */
    public void deleteUser(User user);
    /**
     * Retrieves a User from the database.
     * @param id The id of the User to retrieve.
     * @return The User with the given id, or null if not found.
     */
    public User getUser(int id);
    /**
     * Retrieves all Users from the database.
     * @return A list of all Users in the database.
     */
    public List<User> getAllUsers();
}

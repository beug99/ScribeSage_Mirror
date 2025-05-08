package com.example.addressbook.model;

import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
import java.sql.Statement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteFolderDAO implements IFolderDAO {
    private Connection connection;

    public SqliteFolderDAO() {
        connection = DatabaseConnection.getInstance("notes.db");
        createTable();
    }

    private void createTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS folders (" +
                    "folderId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT NOT NULL" +
                    ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addFolder(Folder folder) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO folders (name, email) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, folder.getFolderName());
            statement.setString(2, folder.getEmail());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                folder.setFolderId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Folder> getFolderByEmail(String email) {
        List<Folder> folders = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM folders WHERE email = ?"
            );
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Folder folder = new Folder(resultSet.getString("name"));
                folder.setFolderId(resultSet.getInt("folderId"));
                folder.setEmail(resultSet.getString("email"));
                folders.add(folder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return folders;
    }

    @Override
    public Folder getFolderById(Integer id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM folders WHERE folderId = ?"
            );
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Folder folder = new Folder(resultSet.getString("name"));
                folder.setFolderId(resultSet.getInt("folderId"));
                folder.setEmail(resultSet.getString("email"));
                return folder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Folder getFolderByNameAndEmail(String name, String email) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM folders WHERE name = ? AND email = ?"
            );
            statement.setString(1, name);
            statement.setString(2, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Folder folder = new Folder(resultSet.getString("name"));
                folder.setFolderId(resultSet.getInt("folderId"));
                folder.setEmail(resultSet.getString("email"));
                return folder;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
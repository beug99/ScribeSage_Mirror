package com.example.addressbook.model;

import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * An interface for the SqliteFolderDAO.
 */
public interface IFolderDAO {
    void addFolder(Folder folder);
    List<Folder> getFolderByEmail(String email);
    Folder getFolderById(Integer id);
    Folder getFolderByNameAndEmail(String name, String email);
    void deleteFolder(Folder folder); // Added this new method
    void deleteFolder(TreeItem<String> selectedItem);
}



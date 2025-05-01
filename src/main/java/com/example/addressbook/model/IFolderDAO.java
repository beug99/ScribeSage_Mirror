package com.example.addressbook.model;

import java.util.List;

public interface IFolderDAO {
    void addFolder(Folder folder);
    List<Folder> getFoldersByUserId(Integer userId);
    Folder getFolderById(Integer id);
    Folder getFolderByNameAndUserId(String name, Integer userId);
}



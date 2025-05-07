package com.example.addressbook.model;

import java.util.List;

public interface IFolderDAO {
    void addFolder(Folder folder);
    List<Folder> getFolderByEmail(String email);
    Folder getFolderById(Integer id);
    Folder getFolderByNameAndEmail(String name, String email);
}



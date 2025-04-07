package com.example.addressbook.controller;

import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SignUpController {
    @FXML
    private ListView<User> userListView;
    private IUserDAO userDAO;

    public SignUpController(){
        userDAO = new SqliteUserDAO();
    }

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField eMail;
    @FXML
    private PasswordField password;
    @FXML
    private Button signUp;

    /**
     * Method for adding user info from sign up fields into database once "sign-up" button is pressed
     */
    @FXML
    private void onSignUp(){
        userDAO.addUser(new User(firstName.getText(), lastName.getText(), eMail.getText(), password.getText()));
    }
}

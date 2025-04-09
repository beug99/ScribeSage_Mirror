package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

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
    private void onSignUp() throws IOException {
        userDAO.addUser(new User(firstName.getText(), lastName.getText(), eMail.getText(), password.getText()));
        Stage stage = (Stage) signUp.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), HelloApplication.WIDTH, HelloApplication.HEIGHT);
        stage.setScene(scene);
    }
}

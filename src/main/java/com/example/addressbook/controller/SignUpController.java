package com.example.addressbook.controller;

import com.example.addressbook.helper.PasswordHasher;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

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
    @FXML
    private Button backButton;

    /**
     * Method for adding user info from sign up fields into database once "sign-up" button is pressed
     */
    @FXML
    private void onSignUp() throws IOException {
        // fields are blank
        if (firstName.getText().isBlank()|| lastName.getText().isBlank() || eMail.getText().isBlank() || password.getText().isBlank()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign-up Failed");
            alert.setHeaderText(null);
            alert.setContentText("You must input all fields");
            alert.showAndWait();
        }
        // email is invalid
        else if (!isValidEmail(eMail.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign-up Failed");
            alert.setHeaderText(null);
            alert.setContentText("You must input a valid email address");
            alert.showAndWait();
        }
        // password is invalid
        else if (!isValidPassword(password.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Sign-up Failed");
            alert.setHeaderText(null);
            alert.setContentText("You must input a valid password. A valid password is at least 8 characters" +
                    " long, containing a mix of upper-case and lower-case characters, numbers, and symbols.");
            alert.showAndWait();
        }
        else {
            // hash password before storing it using password4j
            String hashedPassword = PasswordHasher.hashPassword(password.getText());
            userDAO.addUser(new User(firstName.getText(), lastName.getText(), eMail.getText(), hashedPassword));

            Stage stage = (Stage) signUp.getScene().getWindow();
            SceneLoader.switchScene(stage, "login-view.fxml", false);
        }
    }

    @FXML
    private void onBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

}

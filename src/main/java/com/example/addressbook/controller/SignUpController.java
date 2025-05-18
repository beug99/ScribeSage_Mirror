package com.example.addressbook.controller;

import com.example.addressbook.helper.PasswordHasher;
import com.example.addressbook.helper.SceneLoader;
import com.example.addressbook.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * A controller class that manages the Signup page of the application and user creation.
 * User specific details are commited to the database depending on input validity. User
 * details include first and last name, email address and password.
 */
public class SignUpController {
    @FXML
    private ListView<User> userListView;
    private IUserDAO userDAO;

    /**
     * Constructs an instance of a user Data Access Object (IUserDAO) for the user information.
     */
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
     * A method for saving user info from sign up fields toto the database once the 'Sign-up' button
     * is pressed. If the fields are invalid, the user is unable to sign up and an alert appears
     * explaining why.
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

    /**
     * A method that loads the applications home page when the home button is clicked.
     * @throws IOException Page fails to load.
     */
    @FXML
    private void onBackToHome() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    /**
     * A method that analyses the email entered by the user. If the email is written in the correct
     * syntax it is valid, else it is invalid.
     * @param email The email entered by the user.
     * @return True if valid, False if invalid.
     */
    private boolean isValidEmail(String email) {
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
    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }
}

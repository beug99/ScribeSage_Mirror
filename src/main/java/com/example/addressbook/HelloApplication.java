package com.example.addressbook;

import com.example.addressbook.helper.SceneLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    // Constants defining the window title and size
    @Override
    public void start(Stage stage) throws IOException {
        SceneLoader.switchScene(stage, "login-view.fxml", false);
    }

    public static void main(String[] args) {
        launch();
    }
}
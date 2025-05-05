package com.example.addressbook.helper;

import com.example.addressbook.HelloApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
// replaces boilerplate code for loading a new scene
public class SceneLoader {
    public static <T> T switchScene(Stage stage, String fxmlFilePath, boolean resizeAble) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFilePath));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("ScribeSage");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(resizeAble);
        stage.show();

        return fxmlLoader.getController();
    }
}

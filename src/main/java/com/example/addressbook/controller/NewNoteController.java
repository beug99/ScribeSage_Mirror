package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class NewNoteController extends  CreateNoteController{

    @FXML
    public TextArea noteTextField;
    public Label currentNoteName;
    public Button homeButton;
    public TextField searchBarID;
    public Button saveButton;


    @FXML
    public void setLabelText(String text) {
        currentNoteName.setText(text);
    }

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException  {
        //TODO Will change the FXML file to the home page once I've merged the project
        //TODO Currently just goes back to previous page

        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("create-note-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) {

    }
}
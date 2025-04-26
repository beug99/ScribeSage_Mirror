package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.web.*;
import java.io.IOException;


public class NewNoteController extends CreateNoteController {

    @FXML
    public Label currentNoteName;
    public Button homeButton;
    public TextField searchBarID;
    public Button saveButton;
    public VBox vBoxForHtmlGui;
    public HTMLEditor htmlEditorGui;
    public Button searchBarButton;
    public TextArea todeletejustdisplay;

    @FXML
    public void setLabelText(String text) {
        //This sets the note name displayed to the note name just created
        currentNoteName.setText(text);
    }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {
        //TODO This code needs to be changed to save the data to a DB
        //TODO It currently just displays the html string in textarea so
        //TODO Yas know what it does - setText(htmlEditorGui.getHtmlText()) will probs
        //TODO Be the way to save it to the string/Note DB

        todeletejustdisplay.setText(htmlEditorGui.getHtmlText());
    }

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException  {
        // ------------------------- //
        //TODO I will change the FXML file to the home page once I've merged the project
        //TODO Currently just goes back to previous page
        // ------------------------- //
        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

    }

    @FXML
    public void searchBarButtonClick(ActionEvent actionEvent) {
        //TODO Create a search function - for a later sprint

    }


    // ------------------------------ //
    // ------------------------------ //
    //The below will be deleted - just there to demonstrate conversion ATM
    // ------------------------------ //
    @FXML
    public Button htmlToText = new Button("Convert HTML to Text");

    // ------------------------------ //
    //The below two will be deleted - just there to demonstrate conversion ATM
    @FXML
    public void htmlToTextButtonClick(ActionEvent actionEvent) throws IOException
    {
        htmlEditorGui.setHtmlText(todeletejustdisplay.getText());
    }

}
package com.example.addressbook.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class NewNoteController extends  CreateNoteController{

    @FXML
    public TextArea noteTextField;
    public Label currentNoteName;
    public Button homeButton;
    public TextField searchBarID;


    @FXML
    public void setLabelText(String text) {
        currentNoteName.setText(text);
    }


}

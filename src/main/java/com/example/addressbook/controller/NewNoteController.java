package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.model.AIService;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.SqliteNoteDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private HTMLEditor htmlEditor;
    @FXML
    private Button aiSendPrompt;
    @FXML
    private ProgressIndicator progressIndicator;

    private AIService aiService;
    private ExecutorService executorService;

    private SqliteNoteDAO noteDOA;
    private Note currentNote;


    // gets highlighted text
    public String getSelectedHTMLText() {
        WebView webView = (WebView) htmlEditorGui.lookup("WebView");
        if (webView != null) {
            WebEngine engine = webView.getEngine();
            Object result = engine.executeScript("window.getSelection().toString");
            if (result != null) {
                return result.toString(); // Safe casting
            }
        }
        return "";
    }

    // replaced selected text with AI enhanced version
    public void replaceSelectedHTMLText(String replacement) {
        WebView webView = (WebView) htmlEditorGui.lookup("WebView");
        if (webView != null) {
            WebEngine engine = webView.getEngine();
            String escaped = replacement.replace("'", "\\'").replace("\n", "\\n");
            String script = "var sel = window.getSelection();" +
                    "if (sel.rangeCount > 0) {" +
                    "  var range = sel.getRangeAt(0);" +
                    "  range.deleteContents();" +
                    "  var el = document.createElement('span');" +
                    "  el.innerHTML = '" + escaped + "';" +
                    "  range.insertNode(el);" +
                    "}";
            engine.executeScript(script);
        }
    }

    @FXML
    public void onEnhanceButton(ActionEvent event) {
        String selected = getSelectedHTMLText();
        if (selected == null || selected.trim().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING, "Select some text, Silly!");
            alert.setHeaderText(null);
            alert.showAndWait();
            return;
        }
        String enhanced = aiService.enhanceText(selected); // get ai Service
        replaceSelectedHTMLText(enhanced);
    }

    @FXML
    public void onGetHighlighted(ActionEvent event){
        String selectedText = getSelectedHTMLText();
        System.out.println("Selected text: " + selectedText);
        // Example: Show in alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selected text:\n" + selectedText);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public NewNoteController() {
        super();
        aiService = new AIService();
        noteDOA = new SqliteNoteDAO();
    }

    @FXML
    public void setLabelText(String text) {
        //This sets the note name displayed to the note name just created
        currentNoteName.setText(text);
    }

    @FXML
    public void setCurrentNote(Note note) {
        currentNote = note;
        System.out.println("Note set in NewNoteController: " + currentNote.getNoteName());
        if (currentNoteName != null && htmlEditorGui != null) {
            currentNoteName.setText(currentNote.getNoteName());
            htmlEditorGui.setHtmlText(currentNote.getNoteText());
            System.out.println("UI updated from setCurrentNote().");
        }

    }

    @FXML
    public void initialize() {
        System.out.println("Initialising NewNoteController");
        if(currentNote != null) {
            currentNoteName.setText(currentNote.getNoteName());
            htmlEditorGui.setHtmlText(currentNote.getNoteText());
            System.out.println("Trying to put note content in text field: " + currentNote.getNoteText());
        } else {
            System.out.println("Current Note is null");
        }
     }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("Attempting to save: Note ID_" + currentNote.getId() + " Note name_" + currentNote.getNoteName());
        String updatedContent = htmlEditorGui.getHtmlText();
        currentNote.setNoteText(updatedContent);
        noteDOA.updateNote(currentNote);

        todeletejustdisplay.setText(htmlEditorGui.getHtmlText());
    }

    @FXML
    public void onLoadButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("Load button pressed");
        //TODO Load another view with sole purpose to display notes associated with owner
    }

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException  {

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
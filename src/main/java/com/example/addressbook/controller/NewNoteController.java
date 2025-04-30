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
    private Button enhanceTextButton;
    @FXML
    private ProgressIndicator progressIndicator; // Add this to FXML

    private AIService aiService;
    private ExecutorService executorService;
    private SqliteNoteDAO noteDOA;
    private Note currentNote;

    public NewNoteController() {
        super();
        aiService = new AIService();
        noteDOA = new SqliteNoteDAO();
        // Create a thread pool for background tasks
        executorService = Executors.newFixedThreadPool(2);
    }

    /**
     * Gets currently selected text from the HTML editor
     */
    public String getSelectedHTMLText() {
        WebView webView = (WebView) htmlEditorGui.lookup("WebView");
        if (webView != null) {
            WebEngine engine = webView.getEngine();
            Object result = engine.executeScript("window.getSelection().toString()");
            if (result != null) {
                return result.toString();
            }
        }
        return "";
    }

    /**
     * Replaces the selected text in the HTML editor with new content
     */
    public void replaceSelectedHTMLText(String replacement) {
        if (replacement == null || replacement.isEmpty()) {
            return;
        }

        WebView webView = (WebView) htmlEditorGui.lookup("WebView");
        if (webView != null) {
            WebEngine engine = webView.getEngine();
            // Properly escape the replacement text to avoid JavaScript injection issues
            String escaped = replacement.replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

            String script = "var sel = window.getSelection();" +
                    "if (sel.rangeCount > 0) {" +
                    "  var range = sel.getRangeAt(0);" +
                    "  range.deleteContents();" +
                    "  var el = document.createElement('span');" +
                    "  el.innerHTML = '" + escaped + "';" +
                    "  range.insertNode(el);" +
                    "  sel.removeAllRanges();"+
                    "}";
            engine.executeScript(script);
        }
    }

    @FXML
    public void onEnhanceButton(ActionEvent event) {
        String selected = getSelectedHTMLText();
        if (selected == null || selected.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "No text selected",
                    "Please select some text to enhance.");
            return;
        }
        // disable the enhance button and show progress indicator
        enhanceTextButton.setDisable(true);
        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }

        // create a task to run the AI service in background
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return aiService.enhanceText(selected);
            }

            @Override
            protected void succeeded() {
                String enhanced = getValue();
                Platform.runLater(() -> {
                    replaceSelectedHTMLText(enhanced);
                    enhanceTextButton.setDisable(false);
                    if (progressIndicator != null) {
                        progressIndicator.setVisible(false);
                    }
                });
            }

            @Override
            protected void failed() {
                Throwable exception = getException();
                Platform.runLater(() -> {
                    showAlert(AlertType.ERROR, "Enhancement Failed",
                            "Could not enhance text: " +
                                    (exception != null ? exception.getMessage() : "Unknown error"));
                    enhanceTextButton.setDisable(false);
                    if (progressIndicator != null) {
                        progressIndicator.setVisible(false);
                    }
                });
            }
        };
        // Run the task in the background
        executorService.submit(task);
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void onGetHighlighted(ActionEvent event) {
        String selectedText = getSelectedHTMLText();
        if (selectedText != null && !selectedText.isEmpty()) {
            showAlert(AlertType.INFORMATION, "Selected Text", selectedText);
        } else {
            showAlert(AlertType.INFORMATION, "No Selection", "No text is currently selected.");
        }
    }

    @FXML
    public void setLabelText(String text) {
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
        System.out.println("Initializing NewNoteController");

        // Hide progress indicator initially
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        if (currentNote != null) {
            currentNoteName.setText(currentNote.getNoteName());
            htmlEditorGui.setHtmlText(currentNote.getNoteText());
            System.out.println("Trying to put note content in text field: " + currentNote.getNoteText());
        } else {
            System.out.println("Current Note is null");
        }
    }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {
        if (currentNote == null) {
            showAlert(AlertType.ERROR, "Save Error", "No note is currently loaded.");
            return;
        }

        System.out.println("Attempting to save: Note ID_" + currentNote.getId() + " Note name_" + currentNote.getNoteName());
        String updatedContent = htmlEditorGui.getHtmlText();
        currentNote.setNoteText(updatedContent);

        try {
            noteDOA.updateNote(currentNote);
            if (todeletejustdisplay != null) {
                todeletejustdisplay.setText(htmlEditorGui.getHtmlText());
            }
            showAlert(AlertType.INFORMATION, "Note Saved", "Your note has been saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Save Failed",
                    "Could not save the note: " + e.getMessage());
        }
    }

    @FXML
    public void onLoadButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("Load button pressed");
        //TODO Load another view with sole purpose to display notes associated with owner
    }

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
    }

    @FXML
    public void searchBarButtonClick(ActionEvent actionEvent) {
        //TODO Create a search function - for a later sprint
    }

    @FXML
    public void htmlToTextButtonClick(ActionEvent actionEvent) throws IOException {
        htmlEditorGui.setHtmlText(todeletejustdisplay.getText());
    }
}
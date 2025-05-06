package com.example.addressbook.controller;

import com.example.addressbook.HelloApplication;
import com.example.addressbook.Session;
import com.example.addressbook.model.AIService;
import com.example.addressbook.model.Note;
import com.example.addressbook.model.SqliteNoteDAO;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Stop;
import javafx.scene.web.HTMLEditor;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Timer;
import static java.util.concurrent.TimeUnit.*;

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
    public Label nameLabel;
    public CheckBox enableAutoSaveCheckbox;
    public Button enhanceTextButton;
    public ProgressIndicator progressIndicator; // Add to FXML

    private AIService aiService;
    private ExecutorService executorService;
    private SqliteNoteDAO noteDOA;
    private Note currentNote;
    private boolean autoSavingEnabled = false;
    private Timer autoSaveTimer;
    private int AutoSaveInterval = 5; // How often the autosave runs in minutes

    public NewNoteController() {
        super();
        aiService = new AIService();
        noteDOA = new SqliteNoteDAO();
        // Create a thread pool for background tasks
        executorService = Executors.newFixedThreadPool(2);
    }

    @FXML
    public void initialize() {
        String fullName = Session.getFirstName() + " " + Session.getLastName();
        nameLabel.setText(fullName);

        System.out.println("Initializing NewNoteController");
        // Hide progress indicator initially
        if (progressIndicator != null) {
            progressIndicator.setVisible(false);
        }

        if (currentNote != null) {
            currentNoteName.setText(currentNote.getNoteName());
            htmlEditorGui.setHtmlText(currentNote.getNoteText());
            htmlEditorGui.setStyle("");
            System.out.println("Trying to put note content in text field: " + currentNote.getNoteText());
            // Auto save on note initialization
            saveNote();
        } else {
            System.out.println("Current Note is null");
        }

        // Listener for enabling/disabling autosave
        enableAutoSaveCheckbox.selectedProperty().addListener((observable, oldVal, newVal) ->{
            autoSavingEnabled = newVal;
            if (autoSavingEnabled) {
                StartAutoSave();
            } else {
                StopAutoSave();
            }
        });
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

    /**
     * Uses the intergraded AI to enhance the text
     */
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
    public void onSaveButtonClick(ActionEvent actionEvent) throws IOException {
        if(saveNote()) {
            showAlert(AlertType.INFORMATION, "Note Saved", "Your note has been saved successfully.");
        }
    }

    @FXML
    public void onLoadButtonClick(ActionEvent actionEvent) throws IOException {
        System.out.println("Load button pressed");
        //TODO Load another view with sole purpose to display notes associated with owner
    }

    @FXML
    public void onHomeButtonClick(ActionEvent actionEvent) throws IOException {
        StopAutoSave(); // Otherwise it will keep running in the background, even after you x out
        Stage stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("homepage-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(false);

        stage.show();
    }

    @FXML
    public void searchBarButtonClick(ActionEvent actionEvent) {
        //TODO Create a search function - for a later sprint
    }

    @FXML
    public void htmlToTextButtonClick(ActionEvent actionEvent) throws IOException {
        htmlEditorGui.setHtmlText(todeletejustdisplay.getText());
    }

    private void refreshHTMLEditor() {
        // Store current content
        String currentContent = htmlEditorGui.getHtmlText();

        // Get the WebView within the HTMLEditor
        WebView webView = (WebView) htmlEditorGui.lookup("WebView");
        if (webView != null) {
            // Ensure the WebView fills its container
            webView.setPrefHeight(htmlEditorGui.getHeight() - 80); // Subtract toolbar height
            webView.setMinHeight(200); // Set minimum height

            // Set the background color explicitly
            webView.setStyle("-fx-background-color: white;");

            // Apply the changes
            Platform.runLater(() -> {
                // Force redraw with current content
                htmlEditorGui.setHtmlText(currentContent);

                // Execute JavaScript to ensure body fills the available space
                WebEngine engine = webView.getEngine();
                engine.executeScript(
                        "document.body.style.backgroundColor = 'white';" +
                                "document.body.style.height = '100%';" +
                                "document.body.style.margin = '0';" +
                                "document.body.style.padding = '5px';"
                );
            });
        }
    }

    /**
     * Starts a timer to save the note every 5 minutes
     */
    private void StartAutoSave() {
        System.out.println("Auto Saving Enabled");
        autoSaveTimer = new Timer(true); // isDaemon will terminate when app gets x'd
        autoSaveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Autosaving...");
                Platform.runLater(() -> saveNote());
            }
        }, 1000, MILLISECONDS.convert(AutoSaveInterval, MINUTES)); //
    }

    /**
     * Ends and deletes the save so it can be created again when the autosave is started
     */
    private void StopAutoSave() {
        if(autoSaveTimer != null) {
            autoSaveTimer.cancel();
            autoSaveTimer = null;
            System.out.println("Autosaving Disabled");
        }
    }

    /**
     * Saves the state of the current note to the notes DB
     */
    private boolean saveNote() {
        if (currentNote == null) {
            showAlert(AlertType.ERROR, "Save Error", "No note is currently loaded.");
            return false;
        }
        String updatedContent = htmlEditorGui.getHtmlText();
        currentNote.setNoteText(updatedContent);

        try {
            noteDOA.updateNote(currentNote);
            if (todeletejustdisplay != null) {
                todeletejustdisplay.setText(htmlEditorGui.getHtmlText());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Save Failed",
                    "Could not save the note: " + e.getMessage());
        }
        return false;
    }

    public void toggleNavMenu(MouseEvent mouseEvent) {

    }
    /**
     * Private method to show alerts
     */
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
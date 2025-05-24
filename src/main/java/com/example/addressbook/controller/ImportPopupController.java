package com.example.addressbook.controller;

import com.example.addressbook.service.ImportService;
import com.example.addressbook.service.VoskTranscribeService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;


public class ImportPopupController {
    @FXML
    private TextArea importArea;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button copyButton;

    @FXML
    private StackPane stackPane;

    private ProgressIndicator progressIndicator;

    /**
     * initialises the import controller elements, including the progress indicator
     */
    @FXML
    public void initialize() {
        // progress indicator
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        progressIndicator.setMaxSize(50, 50);

        // add to stack pane if it exists
        if (stackPane != null) {
            stackPane.getChildren().add(progressIndicator);
        }
    }

    /**
     * handles file selection, including .txt and .pdf files
     */
    @FXML
    public void onChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File to Import");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Files", "*.txt", "*.pdf"),
                new FileChooser.ExtensionFilter("Text files", "*.txt"),
                new FileChooser.ExtensionFilter("PDF files", "*.pdf")
        );

        File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (file != null) {
            loadFile(file);
        }
    }

    /**
     * handles file loading for imports
     * @param file the selected file
     */
    private void loadFile(File file) {
        // show progress indicator
        if (progressIndicator != null) {
            progressIndicator.setVisible(true);
        }
        chooseFileButton.setDisable(true);
        copyButton.setDisable(true);
        importArea.setPromptText("Loading file...");

        // create background task for file loading with PDFBoard
        Task<String> loadTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return ImportService.importFile(file.getAbsolutePath());
            }

            /**
             * handles successful file import
             */
            @Override
            protected void succeeded() {
                String content = getValue();
                importArea.setText(content);
                importArea.setPromptText("Your imported file will appear here");

                // Show success message with file info
                String fileType = ImportService.getFileTypeDescription(file.getAbsolutePath());
                String message = String.format("Successfully loaded %s (%d characters)",
                        fileType, content.length());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("File Loaded");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();

                cleanup();
            }

            /**
             * handles failed file import
             */
            @Override
            protected void failed() {
                Throwable exception = getException();
                String errorMessage = "Could not read file: " + exception.getMessage();

                if (exception instanceof IOException) {
                    if (exception.getMessage().contains("encrypted")) {
                        errorMessage = "This PDF is password protected and cannot be read.";
                    } else if (exception.getMessage().contains("no extractable text")) {
                        errorMessage = "This PDF contains no readable text (it may be scanned images).";
                    }
                }

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Error");
                alert.setHeaderText(null);
                alert.setContentText(errorMessage);
                alert.showAndWait();

                importArea.setText("");
                importArea.setPromptText("Your imported file will appear here");
                cleanup();
            }
            /**
             * handles file cleanup
             */
            private void cleanup() {
                if (progressIndicator != null) {
                    progressIndicator.setVisible(false);
                }
                chooseFileButton.setDisable(false);
                copyButton.setDisable(false);
            }
        };

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    /**
     * allows user to copy the imported file's content
     */
    @FXML
    public void onCopyImport() {
        String text = importArea.getText();
        if (text != null && !text.trim().isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Copied");
            alert.setHeaderText(null);
            alert.setContentText(String.format("Content copied to clipboard (%d characters)", text.length()));
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Content");
            alert.setHeaderText(null);
            alert.setContentText("There is no content to copy. Please load a file first.");
            alert.showAndWait();
        }
    }
}
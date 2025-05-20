package com.example.addressbook.controller;

import com.example.addressbook.service.VoskTranscribeService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import java.io.File;
import java.io.IOException;

public class TranscriptionPopupController {
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private StackPane transcriptionStackPane;

    @FXML
    private TextArea transcriptArea;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button copyButton;

    @FXML
    public void onChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.flac")
        );
        File file = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
        if (file != null) {
            progressIndicator.setVisible(true);
            transcriptArea.setText("");

            new Thread(() -> {
                String transcript = VoskTranscribeService.transcribeAudio(file);

                // Update UI on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    transcriptArea.setText(transcript);
                    progressIndicator.setVisible(false);
                });
            }).start();
        }
    }


    @FXML
    public void onCopyTranscript() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(transcriptArea.getText());
        clipboard.setContent(content);
    }
}

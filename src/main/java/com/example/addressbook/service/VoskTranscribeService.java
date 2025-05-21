package com.example.addressbook.service;

import org.vosk.LibVosk;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VoskTranscribeService {
    public static String transcribeAudio(File audioFile) {
        StringBuilder transcript = new StringBuilder();

        try {
            // Get the model from resources folder
            URL modelUrl = VoskTranscribeService.class.getClassLoader()
                    .getResource("vosk-model-small-en-us-0.15");

            if (modelUrl == null) {
                throw new IOException("Could not find Vosk model in resources");
            }

            String modelPath = Paths.get(modelUrl.toURI()).toString();

            try (Model model = new Model(modelPath)) {
                try (AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile)) {

                    AudioFormat format = ais.getFormat();
                    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                        throw new IllegalArgumentException("Only PCM WAV files are supported.");
                    }

                    try (Recognizer recognizer = new Recognizer(model, format.getSampleRate())) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = ais.read(buffer)) >= 0) {
                            if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                                transcript.append(recognizer.getResult()).append("\n");
                            }
                        }
                        transcript.append(recognizer.getFinalResult());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error transcribing audio: " + e.getMessage();
        }
        // use a regex pattern to filter out empty text/vosk syntax
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(transcript.toString());

        if (matcher.find()) {
            return matcher.group(1);

        } else{
            return "No transcribed text found.";
        }
    }
}

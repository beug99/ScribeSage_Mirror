package com.example.addressbook.service;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ImportService {

    public static String importFile(String filePath) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            return importPdfFile(filePath);
        } else if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
            return importTextFile(filePath);
        } else {
            // try to read as text file for other extensions
            return importTextFile(filePath);
        }
    }

    private static String importTextFile(String filePath) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private static String importPdfFile(String filePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(filePath))) {
            if (document.isEncrypted()) {
                throw new IOException("PDF is encrypted and cannot be read");
            }

            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);

            if (text == null || text.trim().isEmpty()) {
                throw new IOException("PDF contains no extractable text");
            }

            return text;
        }
    }

    public static String getFileTypeDescription(String filePath) {
        String fileName = filePath.toLowerCase();
        if (fileName.endsWith(".pdf")) {
            return "PDF Document";
        } else if (fileName.endsWith(".txt") || fileName.endsWith(".text")) {
            return "Text File";
        } else {
            return "Unknown File Type";
        }
    }
}

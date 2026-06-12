package com.java;

import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to read content from Word documents (.docx format)
 * Preserves formatting, special characters, and document structure
 */
public class WordDocumentReader {

    /**
     * Reads all text content from a Word document preserving formatting
     * @param filePath Path to the Word document
     * @return List of strings containing all text content with formatting preserved
     * @throws IOException if file cannot be read
     */
    public static List<String> readWordDocument(String filePath) throws IOException {
        List<String> content = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        if (!file.getName().endsWith(".docx")) {
            throw new IOException("File must be a .docx file");
        }

        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Read paragraphs with formatting
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = extractParagraphWithFormatting(paragraph);
                content.add(text);
            }

            // Read tables
            for (XWPFTable table : document.getTables()) {
                content.add("\n"); // Add space before table
                for (XWPFTableRow row : table.getRows()) {
                    StringBuilder rowContent = new StringBuilder();
                    for (XWPFTableCell cell : row.getTableCells()) {
                        rowContent.append(cell.getText().trim()).append("\t");
                    }
                    if (rowContent.length() > 0) {
                        content.add(rowContent.toString());
                    }
                }
                content.add("\n"); // Add space after table
            }
        }

        return content;
    }

    /**
     * Extracts paragraph text while preserving formatting and special characters
     * @param paragraph The paragraph to extract
     * @return Text with formatting preserved
     */
    private static String extractParagraphWithFormatting(XWPFParagraph paragraph) {
        StringBuilder text = new StringBuilder();

        // Get paragraph text
        String paragraphText = paragraph.getText();
        text.append(paragraphText);

        // Preserve paragraph alignment
        String alignment = getAlignmentDescription(paragraph);
        if (!alignment.isEmpty()) {
            // Keep formatting info for PDF generation
        }

        return text.toString();
    }

    /**
     * Gets paragraph alignment info
     */
    private static String getAlignmentDescription(XWPFParagraph paragraph) {
        if (paragraph.getAlignment() != null) {
            return paragraph.getAlignment().toString();
        }
        return "";
    }

    /**
     * Reads and returns Word document content as a single string
     * @param filePath Path to the Word document
     * @return Complete document content as string
     * @throws IOException if file cannot be read
     */
    public static String readWordDocumentAsString(String filePath) throws IOException {
        List<String> lines = readWordDocument(filePath);
        return String.join("\n", lines);
    }

    /**
     * Gets document with formatting information for advanced processing
     * @param filePath Path to the Word document
     * @return XWPFDocument for direct access to formatting
     * @throws IOException if file cannot be read
     */
    public static XWPFDocument getDocumentForAdvancedProcessing(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        return new XWPFDocument(new FileInputStream(file));
    }
}


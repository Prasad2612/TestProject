package com.java;

import java.io.IOException;
import java.util.List;

/**
 * Main service class that coordinates Word document reading and PDF generation
 */
public class WordToPDFConverter {

    /**
     * Converts a Word document to PDF format
     *
     * @param wordFilePath  Path to the source Word document (.docx)
     * @param pdfOutputPath Path where the PDF will be saved
     * @param pdfTitle      Title for the generated PDF (can be null or empty)
     * @throws IOException if conversion fails
     */
    public static void convertWordToPDF(String wordFilePath, String pdfOutputPath, String pdfTitle) throws IOException {
        try {
            System.out.println("Starting Word to PDF conversion...");
            System.out.println("Reading Word document: " + wordFilePath);
            System.out.println("Processing document with formatting preservation...");

            // Use advanced PDF generation to preserve formatting, special characters, and structure
            PDFGenerator.generatePDFFromWordDocument(wordFilePath, pdfOutputPath, pdfTitle);


            System.out.println("✓ Conversion completed successfully!");
            System.out.println("PDF saved to: " + pdfOutputPath);

        } catch (IOException e) {
            System.err.println("✗ Error during conversion: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Converts a Word document to PDF without a title
     *
     * @param wordFilePath  Path to the source Word document (.docx)
     * @param pdfOutputPath Path where the PDF will be saved
     * @throws IOException if conversion fails
     */
    public static void convertWordToPDF(String wordFilePath, String pdfOutputPath) throws IOException {
        convertWordToPDF(wordFilePath, pdfOutputPath, null);
    }

    /**
     * Alternative conversion method using simple text extraction
     * Use this if formatting preservation has issues with special characters
     *
     * @param wordFilePath  Path to the source Word document (.docx)
     * @param pdfOutputPath Path where the PDF will be saved
     * @param pdfTitle      Title for the generated PDF
     * @throws IOException if conversion fails
     */
    public static void convertWordToPDFSimple(String wordFilePath, String pdfOutputPath, String pdfTitle) throws IOException {
        try {
            System.out.println("Starting Word to PDF conversion (simple mode)...");
            System.out.println("Reading Word document: " + wordFilePath);

            // Read content from Word document
            List<String> wordContent = WordDocumentReader.readWordDocument(wordFilePath);

            if (wordContent.isEmpty()) {
                System.out.println("Warning: Word document is empty or contains no readable text.");
            } else {
                System.out.println("Successfully read " + wordContent.size() + " lines from Word document.");
            }

            System.out.println("Generating PDF: " + pdfOutputPath);

            // Generate PDF from the content
            PDFGenerator.generatePDFFromContent(pdfOutputPath, pdfTitle, wordContent);

            System.out.println("✓ Conversion completed successfully!");
            System.out.println("PDF saved to: " + pdfOutputPath);

        } catch (IOException e) {
            System.err.println("✗ Error during conversion: " + e.getMessage());
            throw e;
        }
    }
}


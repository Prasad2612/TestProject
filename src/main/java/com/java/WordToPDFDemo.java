package com.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Example program demonstrating Word to PDF conversion functionality
 */
public class WordToPDFDemo {

    public static void main(String[] args) {
        // Example 1: Convert Word document to PDF
        demonstrateWordToPDFConversion();

        // Example 2: Generate PDF directly from content
        demonstratePDFGeneration();
    }

    /**
     * Demonstrates converting a Word document to PDF
     */
    private static void demonstrateWordToPDFConversion() {
        System.out.println("=".repeat(60));
        System.out.println("Example 1: Word Document to PDF Conversion");
        System.out.println("=".repeat(60));

        try {
            // Define file paths
            String wordFilePath = "input/document.docx";
            String pdfOutputPath = "output/document.pdf";
            String pdfTitle = "Converted Word Document";

            // Check if input file exists
            if (!Files.exists(Paths.get(wordFilePath))) {
                System.out.println("Note: Input Word file not found at: " + wordFilePath);
                System.out.println("Please place a .docx file at the specified location.");
                System.out.println("To use this feature:");
                System.out.println("  1. Create 'input' folder in project root");
                System.out.println("  2. Place a .docx file named 'document.docx'");
                System.out.println("  3. Run this program again\n");
            } else {
                // Perform conversion
                WordToPDFConverter.convertWordToPDF(wordFilePath, pdfOutputPath, pdfTitle);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * Demonstrates generating a PDF with sample content
     */
    private static void demonstratePDFGeneration() {
        System.out.println("=".repeat(60));
        System.out.println("Example 2: Direct PDF Generation");
        System.out.println("=".repeat(60));

        try {
            // Define output path
            String pdfOutputPath = "output/sample.pdf";
            String pdfTitle = "Sample PDF Document";

            // Sample content
            String content = "This is a sample PDF document.\n" +
                           "It demonstrates the PDF generation functionality.\n" +
                           "\n" +
                           "Key Features:\n" +
                           "• Read Word documents (.docx format)\n" +
                           "• Extract text and table content\n" +
                           "• Generate professional PDF files\n" +
                           "• Save to disk automatically\n" +
                           "\n" +
                           "This example shows how easy it is to use the system.";

            // Generate PDF
            PDFGenerator.generatePDFFromString(pdfOutputPath, pdfTitle, content);
            System.out.println("✓ Sample PDF generated successfully!");
            System.out.println("PDF saved to: " + pdfOutputPath);

        } catch (IOException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
        }

        System.out.println();
    }
}


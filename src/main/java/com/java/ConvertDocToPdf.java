package com.java;

import java.io.IOException;

/**
 * Main program to convert a Word document to PDF
 * This program reads a .docx file and generates a corresponding PDF file
 *
 * Run this class to convert: PBP_CV.docx to PBP_CV.pdf
 */
public class ConvertDocToPdf {

    public static void main(String[] args) {
        // Input Word document path
        String wordFilePath = "C:\\Users\\pmspp\\AppData\\Roaming\\JetBrains\\IntelliJIdea2025.2\\extensions\\PRODATA\\PBP_CV.docx";

        // Output PDF path - will be saved in the same directory as input
//        String outputDir = "C:\\Users\\pmspp\\AppData\\Roaming\\JetBrains\\IntelliJIdea2025.2\\extensions\\PRODATA\\";
        String outputDir = "C:\\A\\Projects\\TestProject\\src\\test\\";
        String pdfFilePath = outputDir + "PBP_CV.pdf";

        // PDF title
        String pdfTitle = "CV Document - Converted from Word";

        try {
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║         Word Document to PDF Converter                      ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Input File  : " + wordFilePath);
            System.out.println("Output File : " + pdfFilePath);
            System.out.println();

            // Convert Word to PDF
            WordToPDFConverter.convertWordToPDF(wordFilePath, pdfFilePath, pdfTitle);

            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              ✓ Conversion Completed Successfully           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");

        } catch (IOException e) {
            System.err.println();
            System.err.println("╔════════════════════════════════════════════════════════════╗");
            System.err.println("║                    ✗ Conversion Failed                    ║");
            System.err.println("╚════════════════════════════════════════════════════════════╝");
            System.err.println();
            System.err.println("Error Message: " + e.getMessage());
            System.err.println();
            System.err.println("Possible solutions:");
            System.err.println("1. Verify the input file path is correct");
            System.err.println("2. Ensure the file exists and is a valid .docx file");
            System.err.println("3. Check that you have read permissions for the input file");
            System.err.println("4. Check that you have write permissions for the output directory");
            System.err.println();
            e.printStackTrace();
        }
    }
}


package com.java;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service to generate PDF files with preserved formatting
 */
public class PDFGenerator {

    // PDF Generation constants
    private static final float DEFAULT_FONT_SIZE = 11f;
    private static final float TITLE_FONT_SIZE = 16f;

    /**
     * Generates a PDF file from a list of text lines with formatting
     * @param outputPath Path where the PDF will be saved
     * @param title Title of the PDF document
     * @param content List of text lines to include in the PDF
     * @throws IOException if PDF cannot be created
     */
    public static void generatePDFFromContent(String outputPath, String title, List<String> content) throws IOException {
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + parentDir.getAbsolutePath());
            }
        }

        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {

            // Add title if provided
            if (title != null && !title.isEmpty()) {
                Paragraph titleParagraph = new Paragraph(title)
                        .setFontSize(TITLE_FONT_SIZE)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(12);
                doc.add(titleParagraph);

                // Add separator line
                doc.add(new Paragraph("\n"));
            }

            // Add content paragraphs with proper spacing
            for (String line : content) {
                if (line == null || line.trim().isEmpty()) {
                    // Preserve empty lines for formatting
                    doc.add(new Paragraph(" ").setMarginBottom(3));
                } else {
                    Paragraph paragraph = new Paragraph(line)
                            .setFontSize(DEFAULT_FONT_SIZE)
                            .setMarginBottom(6);
                    doc.add(paragraph);
                }
            }
        }
    }

    /**
     * Generates a PDF file from a single text string with formatting
     * @param outputPath Path where the PDF will be saved
     * @param title Title of the PDF document
     * @param content Text content to include in the PDF
     * @throws IOException if PDF cannot be created
     */
    public static void generatePDFFromString(String outputPath, String title, String content) throws IOException {
        List<String> lines = List.of(content.split("\n"));
        generatePDFFromContent(outputPath, title, lines);
    }

    /**
     * Generates a formatted table in PDF with preserved content
     * @param outputPath Path where the PDF will be saved
     * @param title Title of the PDF document
     * @param tableData 2D array representing table data
     * @throws IOException if PDF cannot be created
     */
    public static void generatePDFWithTable(String outputPath, String title, String[][] tableData) throws IOException {
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + parentDir.getAbsolutePath());
            }
        }

        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {

            // Add title
            if (title != null && !title.isEmpty()) {
                Paragraph titleParagraph = new Paragraph(title)
                        .setFontSize(TITLE_FONT_SIZE)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(12);
                doc.add(titleParagraph);
                doc.add(new Paragraph("\n"));
            }

            // Add table
            if (tableData != null && tableData.length > 0) {
                Table table = new Table(tableData[0].length);

                for (String[] row : tableData) {
                    for (String cell : row) {
                        String cellText = cell != null ? cell : "";
                        Paragraph cellParagraph = new Paragraph(cellText)
                                .setFontSize(DEFAULT_FONT_SIZE);
                        table.addCell(cellParagraph);
                    }
                }

                doc.add(table);
            }
        }
    }

    /**
     * Advanced PDF generation with direct Word document access
     * This method processes the Word document directly to preserve formatting
     * @param wordFilePath Path to the source Word document
     * @param pdfOutputPath Path where the PDF will be saved
     * @param title Title for the PDF
     * @throws IOException if conversion fails
     */
    public static void generatePDFFromWordDocument(String wordFilePath, String pdfOutputPath, String title) throws IOException {
        File outputFile = new File(pdfOutputPath);
        File parentDir = outputFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Failed to create output directory: " + parentDir.getAbsolutePath());
            }
        }

        // Use try-with-resources to ensure wordDoc is properly closed
        org.apache.poi.xwpf.usermodel.XWPFDocument wordDoc = null;
        try (PdfWriter writer = new PdfWriter(pdfOutputPath);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {

            // Add title
            if (title != null && !title.isEmpty()) {
                Paragraph titleParagraph = new Paragraph(title)
                        .setFontSize(TITLE_FONT_SIZE)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(12);
                doc.add(titleParagraph);
                doc.add(new Paragraph("\n"));
            }

            // Process Word document with formatting preservation
            wordDoc = WordDocumentReader.getDocumentForAdvancedProcessing(wordFilePath);

            // Read paragraphs with formatting
            for (org.apache.poi.xwpf.usermodel.XWPFParagraph wordPara : wordDoc.getParagraphs()) {
                String paraText = wordPara.getText();

                if (paraText == null || paraText.trim().isEmpty()) {
                    // Preserve empty lines
                    doc.add(new Paragraph(" ").setMarginBottom(3));
                } else {
                    Paragraph pdfPara = new Paragraph(paraText)
                            .setFontSize(DEFAULT_FONT_SIZE)
                            .setMarginBottom(6);
                    doc.add(pdfPara);
                }
            }

            // Read tables with formatting
            for (org.apache.poi.xwpf.usermodel.XWPFTable wordTable : wordDoc.getTables()) {
                doc.add(new Paragraph("\n")); // Space before table

                // Check if table has rows before accessing
                if (wordTable.getRows() != null && !wordTable.getRows().isEmpty()) {
                    int cols = wordTable.getRows().get(0).getTableCells().size();
                    Table pdfTable = new Table(cols);

                    for (org.apache.poi.xwpf.usermodel.XWPFTableRow row : wordTable.getRows()) {
                        for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : row.getTableCells()) {
                            String cellText = cell.getText();
                            Paragraph cellPara = new Paragraph(cellText != null ? cellText : "")
                                    .setFontSize(DEFAULT_FONT_SIZE);
                            pdfTable.addCell(cellPara);
                        }
                    }

                    doc.add(pdfTable);
                }
                doc.add(new Paragraph("\n")); // Space after table
            }
        } finally {
            // Ensure wordDoc is closed even if an exception occurs
            if (wordDoc != null) {
                try {
                    wordDoc.close();
                } catch (IOException e) {
                    System.err.println("Warning: Failed to close Word document: " + e.getMessage());
                }
            }
        }
    }
}


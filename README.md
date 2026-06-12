# Word to PDF Converter - Complete Guide

## 🎯 Overview

This project provides functionality to convert Word documents (.docx) to PDF format. It's designed to work with your CV document located at:

```
C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.docx
```

The converted PDF will be saved to the same directory as:
```
C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.pdf
```

---

## 🚀 Quick Start (3 Steps)

### Option 1: Run from IntelliJ IDEA (Easiest)

1. Open the project in **IntelliJ IDEA**
2. Navigate to: `src → main → java → com → java → ConvertDocToPdf.java`
3. **Right-click** on the file and select **Run 'ConvertDocToPdf.main()'**

**Result:** A PDF file will be generated in the same folder as your Word document.

### Option 2: Run from Command Line

```bash
# Navigate to project directory
cd C:\A\Projects\TestProject

# Build and run
mvn clean compile
mvn exec:java
```

### Option 3: Use Batch Script (Windows)

1. Navigate to: `C:\A\Projects\TestProject`
2. **Double-click** on `run_converter.bat`
3. The script will build and run the program automatically

### Option 4: Use PowerShell Script (Windows)

```powershell
# Open PowerShell and navigate to project directory
cd C:\A\Projects\TestProject

# Run with bypass execution policy
powershell -ExecutionPolicy Bypass -File run_converter.ps1
```

---

## 📋 Prerequisites

Before running the program, ensure you have:

1. **Java 21 or later** installed
   ```bash
   java -version
   ```

2. **Maven** installed and in PATH
   ```bash
   mvn -version
   ```

   If not installed, download from: https://maven.apache.org/download.cgi

3. **The Word document** exists at:
   ```
   C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.docx
   ```

---

## 📁 Project Structure

```
TestProject/
├── pom.xml                          # Maven configuration
├── HOW_TO_RUN.md                   # Detailed instructions
├── run_converter.bat                # Batch script for Windows
├── run_converter.ps1                # PowerShell script for Windows
└── src/
    └── main/
        └── java/
            └── com/
                └── java/
                    ├── ConvertDocToPdf.java         # Main program (RUN THIS!)
                    ├── WordToPDFConverter.java      # Converter service
                    ├── WordDocumentReader.java      # Word reader
                    ├── PDFGenerator.java            # PDF writer
                    ├── WordToPDFDemo.java           # Example usage
                    └── [other existing classes]
```

---

## 🔧 Available Classes and Features

### ConvertDocToPdf.java (Main Program)
Converts your specific CV document to PDF
```bash
mvn exec:java -Dexec.mainClass="com.java.ConvertDocToPdf"
```

### WordToPDFDemo.java (Examples)
Shows various usage examples
```bash
mvn exec:java -Dexec.mainClass="com.java.WordToPDFDemo"
```

### Core Classes

#### WordToPDFConverter
Main converter service
```java
WordToPDFConverter.convertWordToPDF(inputPath, outputPath, title);
```

#### WordDocumentReader
Read Word document content
```java
List<String> content = WordDocumentReader.readWordDocument("path/to/file.docx");
```

#### PDFGenerator
Generate PDF files
```java
PDFGenerator.generatePDFFromString(outputPath, title, content);
PDFGenerator.generatePDFWithTable(outputPath, title, tableData);
```

---

## 📊 What Gets Converted

The converter extracts and preserves:

✅ **Text Content**
- All paragraphs and text formatting (where possible)
- Headings and subheadings

✅ **Tables**
- All table content
- Cell data

✅ **Document Structure**
- Line breaks and spacing
- Document title

---

## 🎨 Expected Output

When you run the program, you'll see:

```
╔════════════════════════════════════════════════════════════╗
║         Word Document to PDF Converter                      ║
╚════════════════════════════════════════════════════════════╝

Input File  : C:\Users\pmspp\AppData\Roaming\JetBrains\...
Output File : C:\Users\pmspp\AppData\Roaming\JetBrains\...

Starting Word to PDF conversion...
Reading Word document: C:\Users\pmspp\...
Successfully read 45 lines from Word document.
Generating PDF: C:\Users\pmspp\...
✓ Conversion completed successfully!
PDF saved to: C:\Users\pmspp\...

╔════════════════════════════════════════════════════════════╗
║              ✓ Conversion Completed Successfully           ║
╚════════════════════════════════════════════════════════════╝
```

---

## ❌ Troubleshooting

### Problem: "Maven command not found"
**Solution:** 
- Install Maven: https://maven.apache.org/download.cgi
- Add Maven to system PATH

### Problem: "File not found: PBP_CV.docx"
**Solution:**
- Verify the file exists at the specified location
- Check file path spelling and capitalization
- Ensure the file is in .docx format (not .doc)

### Problem: "Permission denied"
**Solution:**
- Ensure you have read access to the input file
- Ensure you have write access to the output directory
- Try running as Administrator

### Problem: "Build failed"
**Solution:**
- Ensure Java 21+ is installed: `java -version`
- Delete `target` folder and rebuild: `mvn clean compile`
- Check internet connection (Maven needs to download dependencies)

---

## 🔄 Customizing the Program

To convert a different document, edit `ConvertDocToPdf.java`:

```java
// Change these lines:
String wordFilePath = "C:\\path\\to\\your\\document.docx";
String pdfFilePath = "C:\\path\\to\\output\\document.pdf";
String pdfTitle = "Your Custom Title";
```

Then run the program again.

---

## 📦 Dependencies

The project uses:
- **Apache POI 5.2.3** - Reading Word documents
- **iText7 7.2.5** - PDF generation
- **SLF4J 2.0.9** - Logging

All dependencies are automatically downloaded by Maven.

---

## 💡 Advanced Usage

### Programmatic Conversion
```java
import com.java.WordToPDFConverter;

// In your code
WordToPDFConverter.convertWordToPDF(
    "input.docx",
    "output.pdf",
    "Document Title"
);
```

### Batch Processing
```java
import com.java.WordToPDFConverter;
import java.io.File;

File folder = new File("documents/");
for (File file : folder.listFiles((dir, name) -> name.endsWith(".docx"))) {
    String inputPath = file.getAbsolutePath();
    String outputPath = inputPath.replace(".docx", ".pdf");
    WordToPDFConverter.convertWordToPDF(inputPath, outputPath, "Converted");
}
```

---

## 📝 Maven Build Commands

```bash
# Clean and compile
mvn clean compile

# Run the converter
mvn exec:java

# Run specific class
mvn exec:java -Dexec.mainClass="com.java.ConvertDocToPdf"

# Create executable JAR
mvn clean package

# Run JAR file
java -cp target/TestProject-1.0-SNAPSHOT.jar com.java.ConvertDocToPdf

# View project dependencies
mvn dependency:tree
```

---

## ✨ Features

✅ Fast and efficient conversion
✅ Preserves document structure
✅ Automatic directory creation
✅ Comprehensive error handling
✅ User-friendly console output
✅ Support for tables and complex layouts
✅ Easy to integrate into other projects

---

## 📞 Support

If you encounter issues:
1. Check the Troubleshooting section above
2. Review the error messages in console
3. Verify all prerequisites are installed
4. Ensure the Word document is valid and not corrupted

---

## 📄 License

This project is part of your TestProject workspace.

---

**Last Updated:** June 11, 2026
**Java Version:** 21+
**Maven Version:** 3.6+


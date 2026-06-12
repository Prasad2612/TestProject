# STEP-BY-STEP GUIDE TO RUN WORD TO PDF CONVERTER

## 🎯 YOUR FILE PATHS
- **Input Word File:** `C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.docx`
- **Output PDF File:** `C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\PBP_CV.pdf`

---

## ✅ STEP 1: VERIFY PREREQUISITES

### Check Java Installation
Open Command Prompt and type:
```bash
java -version
```
**Expected:** Java 21 or higher (e.g., `openjdk version "21"`)

### Check Maven Installation
Open Command Prompt and type:
```bash
mvn -version
```
**Expected:** Maven 3.6+ installed

**If Maven is NOT installed:**
1. Download from: https://maven.apache.org/download.cgi
2. Extract to a folder (e.g., `C:\Maven`)
3. Add to Windows PATH:
   - Right-click "This PC" → Properties
   - Click "Advanced system settings"
   - Click "Environment Variables"
   - Add new variable: `MAVEN_HOME = C:\Maven`
   - Edit PATH and add: `%MAVEN_HOME%\bin`
4. Restart Command Prompt and verify: `mvn -version`

---

## ✅ STEP 2: VERIFY WORD FILE EXISTS

1. Open Windows File Explorer
2. Navigate to: `C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\`
3. **Confirm you see: `PBP_CV.docx`**

---

## ✅ STEP 3: PREPARE THE PROJECT

Open Command Prompt and navigate to project:
```bash
cd C:\A\Projects\TestProject
```

### Clean and Compile
```bash
mvn clean compile
```

**Expected output ends with:**
```
[INFO] BUILD SUCCESS
```

---

## 🚀 STEP 4: RUN THE PROGRAM

### METHOD 1: FROM IntelliJ IDEA (EASIEST)

1. Open the project in IntelliJ IDEA
2. Navigate to: `src/main/java/com/java/ConvertDocToPdf.java`
3. Right-click on the file
4. Select: **Run 'ConvertDocToPdf.main()'**
5. Watch the console for success message
6. ✅ PDF will be created in the PRODATA folder

---

### METHOD 2: FROM COMMAND LINE

In Command Prompt (from project directory):
```bash
mvn exec:java -Dexec.mainClass="com.java.ConvertDocToPdf"
```

**Expected Console Output:**
```
╔════════════════════════════════════════════════════════════╗
║         Word Document to PDF Converter                      ║
╚════════════════════════════════════════════════════════════╝

Input File  : C:\Users\pmspp\AppData\Roaming\JetBrains\...
Output File : C:\Users\pmspp\AppData\Roaming\JetBrains\...

Starting Word to PDF conversion...
Reading Word document: C:\Users\pmspp\...
Successfully read X lines from Word document.
Generating PDF: C:\Users\pmspp\...
✓ Conversion completed successfully!
PDF saved to: C:\Users\pmspp\...

╔════════════════════════════════════════════════════════════╗
║              ✓ Conversion Completed Successfully           ║
╚════════════════════════════════════════════════════════════╝
```

---

### METHOD 3: USING BATCH SCRIPT

1. Open Command Prompt
2. Navigate to: `cd C:\A\Projects\TestProject`
3. Run:
```bash
run_converter.bat
```

The batch script will automatically build and run the program.

---

### METHOD 4: CREATE JAR AND RUN

First, create a standalone JAR file:
```bash
mvn clean package
```

Then run:
```bash
java -jar target/word-to-pdf-converter.jar
```

---

## ✅ STEP 5: VERIFY SUCCESS

1. Open Windows File Explorer
2. Navigate to: `C:\Users\pmspp\AppData\Roaming\JetBrains\IntelliJIdea2025.2\extensions\PRODATA\`
3. **Look for: `PBP_CV.pdf`** ✅
4. Open the PDF file to verify content

---

## ❌ TROUBLESHOOTING

### ERROR: "File not found: PBP_CV.docx"
**Solution:**
- Verify the Word file exists in the PRODATA folder
- Check the file path is correct
- Ensure the file extension is `.docx`

### ERROR: "Maven command not found"
**Solution:**
- Install Maven (see Step 1)
- Add Maven to PATH
- Restart Command Prompt

### ERROR: "BUILD FAILURE"
**Solution:**
```bash
# Delete the target folder
rmdir /s /q target

# Rebuild
mvn clean compile
```

### ERROR: "Permission denied"
**Solution:**
- Ensure the PRODATA folder has write permissions
- Try running Command Prompt as Administrator
- Check that the Word document is not locked/open in another application

### ERROR: "The Word document is empty"
**Solution:**
- Verify the Word document has content
- Try opening it in Microsoft Word to ensure it's valid
- Convert a simpler test Word document first

---

## 📊 PROGRAM FLOW

```
1. Program starts
   ↓
2. Reads Word document (PBP_CV.docx)
   ↓
3. Extracts all text and tables
   ↓
4. Generates formatted PDF
   ↓
5. Saves to same directory (PBP_CV.pdf)
   ↓
6. Displays success message
```

---

## 📝 ADDITIONAL COMMANDS

### View Detailed Build Info
```bash
mvn -X clean compile
```

### Check Dependencies
```bash
mvn dependency:tree
```

### Run Demo (Shows other features)
```bash
mvn exec:java -Dexec.mainClass="com.java.WordToPDFDemo"
```

---

## ✨ WHAT GETS CONVERTED

The converter extracts and preserves:
- ✅ All text paragraphs
- ✅ Headings and formatting
- ✅ Tables with all cell content
- ✅ Line breaks and spacing
- ✅ Document title

---

## 🎉 NEXT STEPS

Once you've successfully converted:
1. Open the PDF in Adobe Reader or any PDF viewer
2. Verify all content was converted correctly
3. You can now repeat for other Word documents by modifying `ConvertDocToPdf.java`

---

**Questions?** Check the README.md file for more details!


/* 
 * 
 * PROJECT
 *     Name
 *         Editor
 *     
 *     Code Version
 *         1.2.6
 *     
 *     Description
 *         An editor that supports editing markdown with formatting preview.
 *         
 * COPYRIGHTS
 *     Copyright (C) 2012 by Natusoft AB All rights reserved.
 *     
 * LICENSE
 *     Apache 2.0 (Open Source)
 *     
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     
 *       http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *     
 * AUTHORS
 *     Tommy Svensson (tommy@natusoft.se)
 *         Changes:
 *         2013-05-27: Created!
 *         
 */
package se.natusoft.doc.markdowndoc.editor.functions;

import se.natusoft.doc.markdown.api.Generator;
import se.natusoft.doc.markdown.exception.GenerateException;
import se.natusoft.doc.markdown.generator.PDFGenerator;
import se.natusoft.doc.markdown.generator.options.PDFGeneratorOptions;
import se.natusoft.doc.markdowndoc.editor.ToolBarGroups;
import se.natusoft.doc.markdowndoc.editor.api.EditorFunction;
import se.natusoft.doc.markdowndoc.editor.exceptions.FunctionException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * Provides a function that exports to PDF.
 */
public class ExportToPDFFunction extends AbstractExportFunction implements EditorFunction {
    //
    // Constants
    //

    /** The property key for the last generated PDF file. */
    private static final String GENERATED_PDF_FILE = "generated.pdf.file";

    //
    // Private Members
    //

    /** The toolbar button. */
    private JButton pdfToolbarButton = null;

    /** Holds all input values for the generate PDF meta data dialog. */
    private PDFData pdfData = new PDFData();

    // The following are referenced from GUI callbacks and thus must be part of the instance.

//    /** The PDF meta data / options dialog. */
    private JFrame pdfMetaDataDialog = null;

    /**
     * This holds components that will be added to the pdfMetaDataDialog in perform().
     */
    private class PDFData extends ExportData {

        private ExportDataValue pageSize = new ExportDataTextValue("Page size:", "A4");
        private ExportDataValue title = new ExportDataTextValue("Title:");
        private ExportDataValue subject = new ExportDataTextValue("Subject:");
        private ExportDataValue keywords = new ExportDataTextValue("Keywords:");
        private ExportDataValue author = new ExportDataTextValue("Author:");
        private ExportDataValue version = new ExportDataTextValue("Version:");
        private ExportDataValue copyrightYear = new ExportDataTextValue("Copyright year:");
        private ExportDataValue copyrightBy = new ExportDataTextValue("Copyright by:");
        private ExportDataValue generateTitlePage = new ExportDataSelectValue("Generate title page:");
        private ExportDataValue generateTOC = new ExportDataSelectValue("Generate TOC:");
        private ExportDataValue openResult = new ExportDataSelectValue("Open result:");

        /**
         * Initializes the exportDataValues list with all the fields
         * for easier dynamic access.
         */
        private void loadPDFDataValues() {
            exportDataValues = new LinkedList<ExportDataValue>();
            for (Field field : PDFData.class.getDeclaredFields()) {
                if (field.getType() == ExportDataValue.class) {
                    field.setAccessible(true);
                    try {
                        exportDataValues.add((ExportDataValue)field.get(ExportToPDFFunction.this.pdfData));
                    }
                    catch (Exception e) {
                        System.err.println("ERROR: " + e.getMessage());
                    }
                }
            }
        }
    }

    //
    // Constructors
    //

    /**
     * Creates a new ExportToPDFFunction instance.
     */
    public ExportToPDFFunction() {
        super(GENERATED_PDF_FILE);
        Icon pdfIcon = new ImageIcon(ClassLoader.getSystemResource("icons/mddpdf.png"));
        this.pdfToolbarButton = new JButton(pdfIcon);
        this.pdfToolbarButton.setToolTipText("Export as PDF (Ctrl-P)");
        this.pdfToolbarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                perform();
            }
        });
    }

    //
    // Methods
    //

    /**
     * Returns the group name the function belongs to.
     */
    @Override
    public String getGroup() {
        return ToolBarGroups.export.name();
    }

    /**
     * Returns the name of the function.
     */
    @Override
    public String getName() {
        return "Export to PDF";
    }

    /**
     * Returns the function toolbar button.
     */
    @Override
    public JComponent getToolBarButton() {
        return this.pdfToolbarButton;
    }

    /**
     * Returns the down key mast to react on for this function.
     */
    @Override
    public int getDownKeyMask() {
        return KeyEvent.CTRL_MASK;
    }

    /**
     * Returns the key code that activate this function.
     */
    @Override
    public int getKeyCode() {
        return KeyEvent.VK_P;
    }

    /**
     * Executes this function.
     *
     * @throws FunctionException on failure to perform function.
     */
    @Override
    public void perform() throws FunctionException {
        this.exportFile = getExportOutputFile("PDF", "pdf", "pdf");

        if (this.exportFile != null) {
            this.pdfMetaDataDialog = new JFrame("PDF document data");
            this.pdfMetaDataDialog.setLayout(new BorderLayout());

            this.pdfData.loadPDFDataValues();

            JPanel dataLabelPanel = new JPanel(new GridLayout(this.pdfData.exportDataValues.size(),1));
            this.pdfMetaDataDialog.add(dataLabelPanel, BorderLayout.WEST);

            JPanel dataValuePanel = new JPanel(new GridLayout(this.pdfData.exportDataValues.size(),1));
            this.pdfMetaDataDialog.add(dataValuePanel, BorderLayout.CENTER);


            for (ExportDataValue exportDataValue : this.pdfData.exportDataValues) {
                dataLabelPanel.add(exportDataValue.label);
                dataValuePanel.add(exportDataValue.value);
            }

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton generateButton = new JButton("Generate");
            generateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    ExportToPDFFunction.this.pdfMetaDataDialog.setVisible(false);
                    generatePDF();
                }
            });
            buttonPanel.add(generateButton);
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    ExportToPDFFunction.this.pdfMetaDataDialog.setVisible(false);
                }
            });
            buttonPanel.add(cancelButton);

            this.pdfMetaDataDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Set initial values to last saved values for the specified file.
            if (this.editor.getCurrentFile() != null) {
                this.pdfData.loadExportData(this.editor.getCurrentFile());
            }

            this.pdfMetaDataDialog.setVisible(true);
            this.pdfMetaDataDialog.setBounds(
                    this.editor.getGUI().getWindowFrame().getX() + 40,
                    this.editor.getGUI().getWindowFrame().getY() + 40,
                    (int) this.pdfMetaDataDialog.getPreferredSize().getWidth(),
                    (int) this.pdfMetaDataDialog.getPreferredSize().getHeight()
            );
        }
    }

    /**
     * Initiates PDF generation and handles any failures with an error dialog.
     */
    private void generatePDF() {
        try {
            _generatePDF();
        }
        catch (RuntimeException re) {
            re.printStackTrace(System.err);
            JOptionPane.showMessageDialog(
                    this.editor.getGUI().getWindowFrame(), re.getMessage(), "Failed to save PDF!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actually performs the PDF generation using MarkdownDocs PDF generator.
     */
    private void _generatePDF() {
        Generator generator = new PDFGenerator();
        PDFGeneratorOptions pdfOpts = new PDFGeneratorOptions();
        pdfOpts.setResultFile(this.exportFile.getAbsolutePath());
        pdfOpts.setAuthor(this.pdfData.author.getValue());
        pdfOpts.setTitle(this.pdfData.title.getValue());
        pdfOpts.setSubject(this.pdfData.subject.getValue());
        pdfOpts.setKeywords(this.pdfData.keywords.getValue());
        pdfOpts.setVersion(this.pdfData.version.getValue());
        pdfOpts.setCopyright("Copyright © " + this.pdfData.copyrightYear.getValue() + " by " +
            this.pdfData.copyrightBy.getValue());
        pdfOpts.setPageSize(this.pdfData.pageSize.getValue());
        pdfOpts.setGenerateTitlePage(Boolean.valueOf(this.pdfData.generateTitlePage.getValue()));
        pdfOpts.setGenerateTOC(Boolean.valueOf(this.pdfData.generateTOC.getValue()));

        BufferedOutputStream pdfStream = null;
        try {
            pdfStream = new BufferedOutputStream(new FileOutputStream(this.exportFile));
            generator.generate(getMarkdownDocument(), pdfOpts, null, pdfStream);
            pdfStream.flush();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
        catch (GenerateException ge) {
            throw new RuntimeException(ge.getMessage(), ge);
        }
        finally {
            try {if (pdfStream!= null) pdfStream.close();} catch (IOException cioe) {}
        }

        if (this.editor.getCurrentFile() != null) {
            this.pdfData.saveExportData(this.editor.getCurrentFile());
        }

        if (this.pdfData.openResult.getValue().equals("true")) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(this.exportFile);
            }
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(
                        this.editor.getGUI().getWindowFrame(), ioe.getMessage(), "Failed to open PDF!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Cleanup and unregister any configs.
     */
    public void close() {}
}
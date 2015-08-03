/*
 *
 * PROJECT
 *     Name
 *         MarkdownDocEditor
 *
 *     Code Version
 *         1.4
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
package se.natusoft.doc.markdowndoc.editor.functions

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.jetbrains.annotations.NotNull
import se.natusoft.doc.markdown.api.Generator
import se.natusoft.doc.markdown.exception.GenerateException
import se.natusoft.doc.markdown.generator.PDFGenerator
import se.natusoft.doc.markdown.generator.options.PDFGeneratorOptions
import se.natusoft.doc.markdowndoc.editor.ToolBarGroups
import se.natusoft.doc.markdowndoc.editor.api.ConfigProvider
import se.natusoft.doc.markdowndoc.editor.api.Configurable
import se.natusoft.doc.markdowndoc.editor.api.EditorFunction
import se.natusoft.doc.markdowndoc.editor.config.ConfigEntry
import se.natusoft.doc.markdowndoc.editor.config.KeyConfigEntry
import se.natusoft.doc.markdowndoc.editor.config.KeyboardKey
import se.natusoft.doc.markdowndoc.editor.exceptions.FunctionException
import se.natusoft.doc.markdowndoc.editor.functions.export.*
import se.natusoft.doc.markdowndoc.editor.gui.ColorsTrait
import se.natusoft.doc.markdowndoc.editor.gui.PopupWindow

import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.border.SoftBevelBorder
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

import static se.natusoft.doc.markdowndoc.editor.config.Constants.CONFIG_GROUP_KEYBOARD

/**
 * Provides a function that exports to PDF.
 */
@CompileStatic
@TypeChecked
class ExportToPDFFunction extends AbstractExportFunction implements EditorFunction, Configurable, ColorsTrait {
    //
    // Constants
    //

    /** The property key for the last generated PDF file. */
    private static final String GENERATED_PDF_FILE = "generated.pdf.file"

    //
    // Private Members
    //

    /** The toolbar button. */
    private JButton pdfToolbarButton = null

    /** Holds all input values for the generate PDF meta data dialog. */
    private PDFData pdfData = new PDFData(getLocalServiceData())

    // The following are referenced from GUI callbacks and thus must be part of the instance.

    /** The PDF meta data / options dialog. */
    private PDFMetaDataDialog pdfMetaDataDialog = new PDFMetaDataDialog()

    //
    // Config
    //

    private static final KeyConfigEntry keyboardShortcutConfig =
            new KeyConfigEntry("editor.function.export.pdf.keyboard.shortcut", "Export PDF keyboard shortcut",
                    new KeyboardKey("Ctrl+P"), CONFIG_GROUP_KEYBOARD)

    private Closure keyboardShortcutConfigChanged = { ConfigEntry ce ->
        updateTooltipText()
    }

    /**
     * Register configurations.
     *
     * @param configProvider The config provider to register with.
     */
    @Override
    void registerConfigs(@NotNull ConfigProvider configProvider) {
        configProvider.registerConfig(keyboardShortcutConfig, keyboardShortcutConfigChanged)
        this.pdfMetaDataDialog.registerConfigs(configProvider)
    }

    /**
     * Unregister configurations.
     *
     * @param configProvider The config provider to unregister with.
     */
    @Override
    void unregisterConfigs(@NotNull ConfigProvider configProvider) {
        configProvider.unregisterConfig(keyboardShortcutConfig, keyboardShortcutConfigChanged)
        this.pdfMetaDataDialog.unregisterConfigs(configProvider)
    }

    /**
     * This holds components that will be added to the pdfMetaDataDialog in perform().
     */
    @CompileStatic
    private class PDFData extends ExportData {

        private ExportDataValue pageSize = new ExportDataTextValue("Page size:", "A4")
        private ExportDataValue title = new ExportDataTextValue("Title:")
        private ExportDataValue subject = new ExportDataTextValue("Subject:")
        private ExportDataValue keywords = new ExportDataTextValue("Keywords:")
        private ExportDataValue author = new ExportDataTextValue("Author:")
        private ExportDataValue version = new ExportDataTextValue("Version:")
        private ExportDataValue copyrightYear = new ExportDataTextValue("Copyright year:")
        private ExportDataValue copyrightBy = new ExportDataTextValue("Copyright by:")
        private ExportDataValue generateSectionNumbers = new ExportDataSelectValue("Generate section numbers:")
        private ExportDataValue generateTitlePage = new ExportDataSelectValue("Generate title page:")
        private ExportDataValue generateTOC = new ExportDataSelectValue("Generate TOC:")
        private ExportDataValue openResult = new ExportDataSelectValue("Open result:")

        PDFData(@NotNull DelayedServiceData delayedServiceData) {
            super(delayedServiceData)
        }
    }

    //
    // Constructors
    //

    /**
     * Creates a new ExportToPDFFunction instance.
     */
    ExportToPDFFunction() {
        super(GENERATED_PDF_FILE)
        Icon pdfIcon = new ImageIcon(ClassLoader.getSystemResource("icons/mddpdf.png"))
        this.pdfToolbarButton = new JButton(pdfIcon)
        this.pdfToolbarButton.addActionListener({ ActionEvent actionEvent -> perform() } as ActionListener)
        updateTooltipText()
    }

    //
    // Methods
    //

    private void updateTooltipText() {
        this.pdfToolbarButton.setToolTipText("Export as PDF (" + keyboardShortcutConfig.getKeyboardKey() + ")")
    }

    /**
     * Returns the group name the function belongs to.
     */
    @Override
    @NotNull String getGroup() {
        ToolBarGroups.EXPORT.name()
    }

    /**
     * Returns the name of the function.
     */
    @Override
    @NotNull String getName() {
        "Export to PDF"
    }

    /**
     * Returns the function toolbar button.
     */
    @Override
    @NotNull JComponent getToolBarButton() {
        this.pdfToolbarButton
    }

    /**
     * Returns the keyboard shortcut for the function.
     */
    @Override
    @NotNull KeyboardKey getKeyboardShortcut() {
        keyboardShortcutConfig.getKeyboardKey()
    }

    /**
     * Executes this function.
     *
     * @throws FunctionException on failure to perform function.
     */
    @Override
    void perform() throws FunctionException {
        this.exportFile = getExportOutputFile("PDF", "pdf", "pdf")

        if (this.exportFile != null) {
            // Set initial values to last saved values for the specified file.
            if (editor.editable.file != null) {
                this.pdfData.loadExportData(editor.editable.file)
            }

            this.pdfMetaDataDialog.open()
        }
    }

    /**
     * Initiates PDF generation and handles any failures with an error dialog.
     */
    private void generatePDF() {
        try {
            _generatePDF()
        }
        catch (RuntimeException re) {
            re.printStackTrace(System.err)
            JOptionPane.showMessageDialog(
                    this.editor.getGUI().getWindowFrame(),
                    re.getMessage(),
                    "Failed to save PDF!",
                    JOptionPane.ERROR_MESSAGE
            )
        }
    }

    /**
     * Actually performs the PDF generation using MarkdownDocs PDF generator.
     */
    private void _generatePDF() {
        Generator generator = new PDFGenerator()
        PDFGeneratorOptions pdfOpts = new PDFGeneratorOptions()
        pdfOpts.setResultFile(this.exportFile.getAbsolutePath())
        pdfOpts.setAuthor(this.pdfData.author.getValue())
        pdfOpts.setTitle(this.pdfData.title.getValue())
        pdfOpts.setSubject(this.pdfData.subject.getValue())
        pdfOpts.setKeywords(this.pdfData.keywords.getValue())
        pdfOpts.setVersion(this.pdfData.version.getValue())
        pdfOpts.setCopyright("Copyright © " + this.pdfData.copyrightYear.getValue() + " by " +
            this.pdfData.copyrightBy.getValue())
        pdfOpts.setPageSize(this.pdfData.pageSize.getValue())
        pdfOpts.setGenerateSectionNumbers(Boolean.valueOf(this.pdfData.generateSectionNumbers.getValue()))
        pdfOpts.setGenerateTitlePage(Boolean.valueOf(this.pdfData.generateTitlePage.getValue()))
        pdfOpts.setGenerateTOC(Boolean.valueOf(this.pdfData.generateTOC.getValue()))

        BufferedOutputStream pdfStream = null
        try {
            pdfStream = new BufferedOutputStream(new FileOutputStream(this.exportFile))
            generator.generate(getMarkdownDocument(), pdfOpts, null, pdfStream)
            pdfStream.flush()
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe)
        }
        catch (GenerateException ge) {
            throw new RuntimeException(ge.getMessage(), ge)
        }
        finally {
            try {if (pdfStream!= null) pdfStream.close()} catch (IOException ignored) {}
        }

        if (this.editor?.editable?.file != null) {
            this.pdfData.saveExportData(this.editor.editable.file)
        }

        if (this.pdfData.openResult.getValue().equals("true")) {
            Desktop desktop = Desktop.getDesktop()
            try {
                desktop.open(this.exportFile)
            }
            catch (IOException ioe) {
                JOptionPane.showMessageDialog(
                        this.editor.getGUI().getWindowFrame(), ioe.getMessage(), "Failed to open PDF!", JOptionPane.ERROR_MESSAGE)
            }
        }
    }


    /**
     * Cleanup and unregister any configs.
     */
    void close() {}

    //
    // Inner Classes
    //

    @CompileStatic
    @TypeChecked
    private class PDFMetaDataDialog extends PopupWindow {

        PDFMetaDataDialog() {
            safeOpacity = popupOpacity

            setLayout(new BorderLayout())

            JPanel borderPanel = new JPanel(new BorderLayout())
            borderPanel.setBorder(new EmptyBorder(5, 5, 5, 5))
            add(borderPanel, BorderLayout.CENTER)
            updateColors(borderPanel)

            pdfData.loadDataValues()

            JPanel dataLabelPanel = new JPanel(new GridLayout(pdfData.exportDataValues.size(),1))
            borderPanel.add(dataLabelPanel, BorderLayout.WEST)
            updateColors(dataLabelPanel)

            JPanel dataValuePanel = new JPanel(new GridLayout(pdfData.exportDataValues.size(),1))
            borderPanel.add(dataValuePanel, BorderLayout.CENTER)
            updateColors(dataValuePanel)

            borderPanel.add(Box.createRigidArea(new Dimension(12, 12)), BorderLayout.EAST)

            pdfData.exportDataValues.each { ExportDataValue exportDataValue ->
                updateColors(exportDataValue.labelComp)

                dataLabelPanel.add(exportDataValue.labelComp)
                dataValuePanel.add(exportDataValue.valueComp)
            }

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER))
            updateColors(buttonPanel)

            JButton generateButton = new JButton("Generate")
            generateButton.addActionListener({ ActionEvent actionEvent ->
                close()
                doGeneratePDF()
            } as ActionListener)
            buttonPanel.add(generateButton)
            JButton cancelButton = new JButton("Cancel")
            cancelButton.addActionListener({ ActionEvent actionEvent -> close() } as ActionListener)
            buttonPanel.add(cancelButton)

            borderPanel.add(buttonPanel, BorderLayout.SOUTH)

        }

        // Closure can access these, and these can access outer class, but closure cannot access outer class
        // directly. Thereby these bounces. Closures also seem to have problems calling private methods of
        // owner class!

        void open() {
            setVisible(true)
            setSize(getPreferredSize())

            Rectangle mainBounds = editor.getGUI().getWindowFrame().getBounds()
            int x = (int)mainBounds.x + (int)(mainBounds.width / 2) - (int)(getWidth() / 2)
            int y = (int)mainBounds.y + 70
            setLocation(x, y)
        }

        void close() {
            setVisible(false)
        }

        void doGeneratePDF() {
            generatePDF()
        }
    }
}

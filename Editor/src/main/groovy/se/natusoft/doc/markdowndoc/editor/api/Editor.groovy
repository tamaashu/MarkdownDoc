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
package se.natusoft.doc.markdowndoc.editor.api

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

import javax.swing.*
import javax.swing.text.BadLocationException
import java.awt.*

/**
 * This is passed to EditorFunction.perform(...).
 */
@CompileStatic
@TypeChecked
interface Editor extends EnvServices, MouseMotionProvider {

    /**
     * Returns the top margin.
     */
    int getTopMargin()

    /**
     * Returns the width of the editorPane.
     */
    int getWidth()

    /**
     * Returns the current file or null if none.
     */
    @Nullable File getCurrentFile()

    /**
     * Sets the current file.
     *
     * @param file The file to set.
     */
    void setCurrentFile(@NotNull File file)

    /**
     * Returns the contents of the editorPane.
     */
    @NotNull String getEditorContent()

    /**
     * Returns the current selection or null if none.
     */
    @Nullable String getEditorSelection()

    /**
     * Returns the current line.
     */
    @NotNull Line getCurrentLine() throws BadLocationException

    /**
     * Set/replace the entire content of the editorPane.
     *
     * @param content The new content to set.
     */
    void setEditorContent(@NotNull String content)

    /**
     * Returns the current caret location.
     */
    @NotNull Point getCaretLocation()

    /**
     * Moves the current caret location.
     *
     * @param location The new location.
     */
    void setCaretLocation(@NotNull Point location)

    /**
     * Returns the caret dot location.
     */
    int getCaretDot()

    /**
     * Sets the caret dot location.
     *
     * @param dot The new dot location to set.
     */
    void setCaretDot(int dot)

    /**
     * Makes the editorPane view visible in the main scrollable view.
     */
    void showEditorComponent()

    /**
     * Makes the specified component visible in the main scrollable view.
     */
    void showOtherComponent(@NotNull JComponent component)

    /**
     * Inserts new text into the editorPane or replaces current selection.
     *
     * @param text The text to insert.
     */
    void insertText(@NotNull String text)

    /**
     * Adds a blank line.
     */
    void addBlankLine()

    /**
     * Moves the cared backwards.
     *
     * @param noChars The number of characters to move caret.
     */
    void moveCaretBack(int noChars)

    /**
     * Moves the caret forward.
     *
     * @param noChars The number of characters to move caret.
     */
    void moveCaretForward(int noChars)

    /**
     * Requests focus for the editorPane.
     */
    void requestEditorFocus()

    /**
     * Opens a new editorPane window.
     */
    void openNewEditorWindow()

    /**
     * Copies the currently selected text.
     */
    void copy()

    /**
     * Cuts the currently selected text.
     */
    void cut()

    /**
     * Pastes the currently copied/cut text.
     */
    void paste()

    /**
     * Enables all buttons in the specified tool bar group.
     *
     * @param groupName The name of the tool bar group to enable.
     */
    void enableToolBarGroup(@NotNull String groupName)

    /**
     * Disables all button in the specified tool bar group.
     *
     * @param groupName The name of the tool bar group to disable.
     */
    void disableToolBarGroup(@NotNull String groupName)

    /**
     * Refreshes styling and formatting of the document.
     */
    public void refreshStyling()

    /**
     * Opens the specified file in the editorPane.
     *
     * @param file The file to open.
     *
     * @throws java.io.IOException
     */
    void loadFile(@NotNull File file) throws IOException

    /**
     * Saves the currently edited file with the specified path.
     *
     * @param file The file path to save to.
     *
     * @throws IOException
     */
    void saveFileAs(@NotNull File file) throws IOException

    /**
     * Opens a file chooser for specifying file to save to.
     *
     * @throws IOException
     */
    void save() throws IOException

    /**
     * Selects the file to edit in the editor view.
     *
     * @param file The file to edit.
     */
    void selectFileForEditing(File file)

    /**
     * Returns the styler for the editorPane.
     */
    @NotNull JTextComponentStyler getStyler()

    /**
     * This gets called when the window is closed. This can be overriden to
     * handle more more actions like exiting the JVM for example.
     */
    void editorClosed()


}

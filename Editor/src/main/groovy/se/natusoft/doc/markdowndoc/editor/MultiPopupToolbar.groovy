/* 
 * 
 * PROJECT
 *     Name
 *         MarkdownDocEditor
 *     
 *     Code Version
 *         1.3.9
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
 *         2014-02-15: Created!
 *         
 */
package se.natusoft.doc.markdowndoc.editor

import groovy.transform.CompileStatic
import se.natusoft.doc.markdowndoc.editor.api.Editor
import se.natusoft.doc.markdowndoc.editor.api.EditorFunction
import se.natusoft.doc.markdowndoc.editor.api.ToolBar

import javax.swing.*
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import java.util.*
import java.util.List

/**
 * A toolbar that will open a java.awt.Window for each toolbar button. It will open
 * when the mouse is at the top of the editorPane window and close if it leaves that area.
 */
@CompileStatic
public class MultiPopupToolbar implements ToolBar {

    //
    // Private Members
    //

    private Editor editor

    private List<String> toolBarGroups = new LinkedList<>()

    private Map<String, List<EditorFunction>> functions = new HashMap<>()

    private List<JWindow> buttonWindows = new LinkedList<>()

    private boolean open = false

    private int calculatedWidth = 0

    private MouseMotionListener mouseMotionListener = null

    //
    // Constructors
    //

    /**
     * Creates a new MultiPopupToolbar.
     */
    public MultiPopupToolbar() {}

    //
    // Methods
    //

    protected mouseMovedHandler(MouseEvent e) {
        int y = e.getY() - this.editor.getGUI().getEditorVisibleY()
        if (y <= getTopMargin() && e.getX() >= 0 && e.getX() <= getEditorWidth()) {
            if (!isOpen()) {
                int toolbarWidth = calculateWidth()
                int x = getParentFrame().getX() + (int)(getParentFrame().getWidth() / 2) - (int)(toolbarWidth / 2)

                int titleBarHeight =
                        (int)(getParentFrame().getBounds().getHeight() - getParentFrame().getContentPane().getBounds().getHeight())
                open(getParentFrame(), x, getParentFrame().getY() + titleBarHeight + 2)
            }
        }
        else {
            if (isOpen()) {
                close()
            }
        }
    }

    /**
     * Provides the toolbar with the editorPane it is associated.
     *
     * @param editor The associated editorPane provided.
     */
    public void attach(Editor editor) {
        this.editor = editor

        this.mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMovedHandler(e)
            }
        }
        this.editor.addMouseMotionListener(this.mouseMotionListener)
    }

    /**
     * Removes the association with the editorPane.
     *
     * @param editor The editorPane to detach from.
     */
    @Override
    public void detach(Editor editor) {
        close()
        editor.removeMouseMotionListener(this.mouseMotionListener)
        this.editor = null
        this.buttonWindows.clear()
    }


    /**
     * Convenience method to get information from associated editorPane.
     */
    private JFrame getParentFrame() {
        return this.editor.getGUI().getWindowFrame()
    }

    /**
     * Convenience method to get information from associated editorPane.
     */
    private int getTopMargin() {
        return this.editor.getTopMargin()
    }

    /**
     * Convenience method to get information from associated editorPane.
     */
    private int getEditorWidth() {
        return this.editor.getWidth()
    }

    /**
     * Opens the toolbar.
     *
     * @param parent The parent window.
     * @param x The X coordinate to open at.
     * @param y The Y coordinate to open at.
     */
    private void open(JFrame parent, int x, int y) {

        boolean create = this.buttonWindows.isEmpty()

        int width = 0
        Iterator<JWindow> buttonIterator = this.buttonWindows.iterator()
        this.toolBarGroups.each { String group ->
            this.functions.get(group)?.each { EditorFunction editorFunction ->
                JWindow buttonWindow
                if (create) {
                    buttonWindow = new JWindow(parent)
                    buttonWindow.setLayout(new BorderLayout())
                    JComponent toolbarButton = editorFunction.getToolBarButton()
                    //toolbarButton.setSize(80, 80)
                    buttonWindow.add(toolbarButton, BorderLayout.CENTER)
                    this.buttonWindows.add(buttonWindow)
                }
                else {
                    buttonWindow = buttonIterator.next()
                }
                buttonWindow.setSize(buttonWindow.getPreferredSize())
                buttonWindow.setLocation(x, y)
                buttonWindow.setVisible(true)
                x += (int)buttonWindow.getSize().width
                width += (int)buttonWindow.getSize().width
            }
            x += 10
            width += 10
        }
        if (this.calculatedWidth == 0) {
            this.calculatedWidth = width
        }

        this.open = true
    }

    /**
     * Closes the toolbar.
     */
    private void close() {
        this.buttonWindows.each { JWindow buttonWindow ->
            buttonWindow.setVisible(false)
        }

        this.open = false
    }

    /**
     * Calculates an estimated width until a real width is provided. When a real width is available it is returned instead.
     */
    private int calculateWidth() {
        int width = this.calculatedWidth
        if (width == 0) {
            this.toolBarGroups.each { String group ->
                this.functions.get(group)?.each { EditorFunction editorFunction ->
                    width += (int)editorFunction.getToolBarButton().getPreferredSize().width
                }
                width = width + 10
            }
        }

        return width
    }

    /**
     * @return true if toolbar is open.
     */
    public boolean isOpen() {
        return this.open
    }

    /**
     * Adds a function to the toolbar.
     *
     * @param function The function to add.
     */
    public void addFunction(EditorFunction function) {
        if (!this.toolBarGroups.contains(function.getGroup())) {
            this.toolBarGroups.add(function.getGroup())
        }

        List<EditorFunction> groupFunctions = this.functions.get(function.getGroup())
        if (groupFunctions == null) {
            groupFunctions = new LinkedList<>()
            this.functions.put(function.getGroup(), groupFunctions)
        }

        groupFunctions.add(function)
    }

    /**
     * Creates the content of the toolbar. This cannot be done until all
     * functions have been provided.
     */
    @Override
    public void createToolBarContent() {
        // This is done by open() and thus nothing needs to be done here.
    }

    /**
     * Enables all buttons in the specified tool bar group.
     *
     * @param group The tool bar group to enable.
     */
    public void disableGroup(String group) {
        List<EditorFunction> functions = this.functions.get(group)
        if (functions != null) {
            functions.each { EditorFunction function ->
                function.getToolBarButton().setEnabled(false)
            }
        }
        else {
            throw new RuntimeException("Cannot disable non existent group '" + group + "'!")
        }
    }

    /**
     * Disables all buttons in the specified tool bar group.
     *
     * @param group The tool bar group to disable.
     */
    public void enableGroup(String group) {
        List<EditorFunction> functions = this.functions.get(group)
        if (functions != null) {
            functions.each { EditorFunction function ->
                function.getToolBarButton().setEnabled(true)
            }
        }
        else {
            throw new RuntimeException("Cannot enable non existent group '" + group + "'!")
        }
    }

}

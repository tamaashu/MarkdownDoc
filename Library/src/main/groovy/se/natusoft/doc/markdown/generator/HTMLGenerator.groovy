/* 
 * 
 * PROJECT
 *     Name
 *         MarkdownDoc Library
 *     
 *     Code Version
 *         1.0
 *     
 *     Description
 *         Parses markdown and generates HTML and PDF.
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
 *         2012-11-16: Created!
 *         
 */
package se.natusoft.doc.markdown.generator

import se.natusoft.doc.markdown.api.Generator
import se.natusoft.doc.markdown.api.Options
import se.natusoft.doc.markdown.exception.GenerateException
import se.natusoft.doc.markdown.generator.options.HTMLGeneratorOptions
import se.natusoft.doc.markdown.model.*

/**
 * This is a generator that generates HTML from a document model.
 */
class HTMLGenerator implements Generator {
    //
    // Private Members
    //

    /** Our options. */

    //
    // Methods
    //

    /**
     * Returns the options class required for this generator.
     */
    @Override
    public Class getOptionsClass() {
        return HTMLGeneratorOptions.class
    }

    /**
     * The main API for the generator. This does the job!
     *
     * @param document The document model to generate from.
     * @param opts The options.
     * @param rootDir The optional root to prefix condfigured path with.
     */
    @Override
    public void generate(Doc document, Options opts, File rootDir) throws IOException, GenerateException {
        HTMLGeneratorOptions options = (HTMLGeneratorOptions)opts
        def writer //= new FileWriter(rootDir != null ? (rootDir.getPath() + File.separator + options.resultFile) : options.resultFile)
        if (rootDir != null) {
            writer = new FileWriter(rootDir.getPath() + File.separator + options.resultFile)
        }
        else {
            writer = new FileWriter(options.resultFile)
        }
        try {
            doGenerate(document, options, writer)
        }
        finally {
            writer.close()
        }
    }

    /**
     * The main API for the generator. This does the job!
     *
     * @param document The document model to generate from.
     * @param options The options.
     */
    private void doGenerate(Doc document, HTMLGeneratorOptions options, Writer writer) throws IOException, GenerateException {

        PrintWriter printWriter = new PrintWriter(writer)
        def html = new HTMLOutput(pw: printWriter)

        printWriter.println("<!DOCTYPE html>")
        html.tagln("html")
        html.tagln("head")
        html.tage("meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"")
        html.ln()
        html.tage("meta name=\"generated-by\" content=\"MarkdownDoc\"")
        html.ln()
        if (options.css != null && options.css.trim().length() > 0) {
            if (options.inlineCSS) {
                html.tagln("style type=\"text/css\"")
                BufferedReader reader = new BufferedReader(new FileReader(options.css))
                String line = reader.readLine()
                while (line != null) {
                    html.doIndent()
                    html.println(line) // We want no <>& translations here.
                    line = reader.readLine()
                }
                reader.close()
                html.etagln("style")
            }
            else {
                html.tage("link href=\"" + options.css + "\" type=\"text/css\" rel=\"stylesheet\"")
                html.ln()
            }
        }
        html.etagln("head")
        html.tagln("body")

        for (DocItem docItem : document.items) {
            switch (docItem.format) {
                case DocFormat.Comment:
                    html.doIndent()
                    html.println("<!--")
                    html.doIndent()
                    html.println("  " + ((Comment)docItem).text)
                    html.doIndent()
                    html.println("-->")
                    break

                case DocFormat.Paragraph:
                    writeParagraph((Paragraph)docItem, html)
                    break

                case DocFormat.Header:
                    writeHeader((Header)docItem, html)
                    break

                case DocFormat.BlockQuote:
                    writeBlockQuote((BlockQuote)docItem, html)
                    break;

                case DocFormat.CodeBlock:
                    writeCodeBlock((CodeBlock)docItem, html)
                    break

                case DocFormat.HorizontalRule:
                    writeHorizontalRule((HorizontalRule)docItem, html)
                    break

                case DocFormat.List:
                    writeList((List)docItem, html)
                    break

                default:
                    throw new GenerateException(message: "Unknown format model in Doc! [" + docItem.getClass().getName() + "]")
            }
        }

        html.etagln("body")
        html.etagln("html")
    }

    private void writeHeader(Header header, HTMLOutput html) {
        html.tagln(header.level.name(), header.text)
    }

    private void writeBlockQuote(BlockQuote blockQuote, HTMLOutput html) {
        html.tagln("blockquote")
        html.tagln("p")
        html.doIndent()
        writeParagraphContent(blockQuote, html)
        html.etagln("p")
        html.etagln("blockquote")
    }

    private void writeCodeBlock(CodeBlock codeBlock, HTMLOutput html) {
        html.tagln("pre")
        html.tagln("code")
        for (DocItem item : codeBlock.items) {
            html.println(item.toString()) // no <>& translations!
        }
        html.etagln("code")
        html.etagln("pre")
    }

    private void writeHorizontalRule(HorizontalRule horizontalRule, HTMLOutput html) {
        html.tage("hr")
    }

    private void writeList(List list, HTMLOutput html) {
        if (list.ordered) {
            html.tagln("ol")
        }
        else {
            html.tagln("ul")
        }
        list.items.each { li ->
            if (li instanceof List) {
                writeList((List)li, html)
            }
            else {
                html.tagln("li")
//                html.doIndent()
                li.items.each { pg ->
                    writeParagraph((Paragraph)pg, html)
                }
                html.etagln("li")
            }
        }
        if (list.ordered) {
            html.etagln("ol")
        }
        else {
            html.etagln("ul")
        }
    }

    private void writeParagraph(Paragraph paragraph, HTMLOutput html) throws GenerateException {
        html.tagln("p")
        html.doIndent()
        writeParagraphContent(paragraph, html)
        html.etagln("p")
    }

    private void writeParagraphContent(Paragraph paragraph, HTMLOutput html) throws GenerateException {
        boolean first = true
        for (DocItem docItem : paragraph.items) {
            if (docItem.renderPrefixedSpace && !first) {
                html.content(" ")
            }
            first = false

            switch (docItem.format) {

                case DocFormat.Code:
                    writeCode((Code)docItem, html)
                    break

                case DocFormat.Emphasis:
                    writeEmphasis((Emphasis)docItem, html)
                    break

                case DocFormat.Strong:
                    writeStrong((Strong)docItem, html)
                    break

                case DocFormat.Image:
                    writeImage((Image)docItem, html)
                    break

                case DocFormat.Link:
                    writeLink((Link)docItem, html)
                    break
                case DocFormat.AutoLink:
                    writeLink((AutoLink)docItem, html)
                    break

                case DocFormat.PlainText:
                    html.content(((PlainText)docItem).text)
                    break

                default:
                    throw new GenerateException(message: "Unknown format model in Doc! [" + docItem.getClass().getName() + "]")
            }
        }
        html.contentln("")
    }

    private void writeCode(Code code, HTMLOutput html) {
        html.tag("code", code.text)
    }

    private void writeEmphasis(Emphasis emphasis, HTMLOutput html) {
        html.tag("em", emphasis.text)
    }

    private void writeStrong(Strong strong, HTMLOutput html) {
        html.tag("strong", strong.text)
    }

    private void writeImage(Image image, HTMLOutput html) {
        html.tage("img src='" + image.url + "' title='" + image.title + "' alt='" + image.text + "'")
    }

    private void writeLink(Link link, HTMLOutput html) {
        html.tag("a src='" + link.url + "' title='" + link.title + "'")
        html.content(link.text)
        html.etag("a")
    }


    //
    // Inner Classes
    //

    /**
     * A small convenience class for writing HTML will auto indentation.
     */
    private static class HTMLOutput {
        //
        // Private Members
        //

        /** The writer to output on. */
        PrintWriter pw

        /** The indentation level */
        private int indent = 0

        //
        // Methods
        //

        /**
         * Output as a start tag.
         *
         * @param tag The name of the tag to output.
         */
        public void tag(String tag) {
            pw.print("<" + tag + ">")
        }

        /**
         * Outputs the tag and content and ends the tag.
         *
         * @param tag The name of the tag to output.
         * @param content The content of the tag.
         */
        public void tag(String tag, String content) {
            pw.print("<" + tag + ">" + content + "</" + tag + ">")
        }

        /**
         * Outputs a content-less tag that is both started and ended.
         *
         * @param tag The tag to output.
         */
        public void tage(String tag) {
            doIndent()
            pw.print("<" + tag + "/>")
        }

        /**
         * Does the same as tag(tag) but adds a newline also.
         *
         * @param tag The tag to output.
         */
        public void tagln(String tag) {
            doIndent()
            pw.println("<" + tag + ">")
            indent = indent + 2
        }

        /**
         * Does the same as tag(tag, content) but adds a newline also.
         *
         * @param tag The tag to output.
         * @param content The content of the tag.
         */
        public void tagln(String tag, String content) {
            doIndent()
            pw.println("<" + tag + ">" + content + "</" + tag + ">")
        }

        /**
         * Outputs the content of a tag. This also translates &lt;, &gt;, & into their entities.
         *
         * @param cont The content to output.
         */
        public void content(String cont) {
            pw.print(cont.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"))
        }

        /**
         * Does the same as content(content) but also adds a newline.
         *
         * @param cont The content to output.
         */
        public void contentln(String cont) {
            doIndent()
            content(cont)
            pw.println()
        }

        /**
         * Outputs a line of text as is.
         *
         * @param text The text to output.
         */
        public void print(String text) {
            pw.print(text)
        }

        /**
         * Outputs a line of text ending with a newline.
         *
         * @param text The text to output.
         */
        public void println(String text) {
            pw.println(text)
        }

        /**
         * Outputs an end tag.
         *
         * @param tag The tag to end.
         */
        public void etag(String tag) {
            pw.print("</" + tag + ">")
        }

        /**
         * Outputs an end tag plus a newline.
         *
         * @param tag The tag to end.
         */
        public void etagln(String tag) {
            indent = indent - 2
            doIndent()
            pw.println("</" + tag + ">")
        }

        /**
         * Outputs a newline.
         */
        public void ln() {
            pw.println()
        }

        /**
         * Outputs indentation at current indent level.
         */
        public void doIndent() {
            this.indent.times {
                pw.print(" ")
            }
        }
    }
}
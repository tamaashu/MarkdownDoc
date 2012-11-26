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
 *         2012-10-29: Created!
 *         
 */
package se.natusoft.doc.markdown.model

/**
 * This represents an image.
 */
class Image extends Link {

    /**
     * Returns the format this model represents.
     */
    @Override
    public DocFormat getFormat() {
        return DocFormat.Image
    }

    public String toString() {
        StringBuilder sb  = new StringBuilder();

        sb.append("<img src='" + url + "'")
        if (title != null) {
            sb.append(" title='" + this.title + "'")
        }

        sb.append(" alt='" + text +  "'/>")

        return sb.toString()
    }

}
/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.collabora.internal;

import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.xwiki.officeimporter.converter.OfficeDocumentFormat;

/**
 * Simple implementation of {@link OfficeDocumentFormat} for the Collabora Office integration.
 *
 * @version $Id$
 * @since 1.6.0
 */
public class CollaboraOfficeDocumentFormat implements OfficeDocumentFormat
{
    private final String extension;

    private final String mediaType;

    /**
     * Create a new format based on the extension of the provided file.
     *
     * @param fileName the file name.
     */
    public CollaboraOfficeDocumentFormat(String fileName)
    {
        this.extension = FilenameUtils.getExtension(fileName);
        this.mediaType = new Tika().detect(fileName);
    }

    @Override
    public String getName()
    {
        return mediaType;
    }

    @Override
    public String getMediaType()
    {
        return mediaType;
    }

    @Override
    public List<String> getExtensions()
    {
        return List.of(this.extension);
    }

    @Override
    public String getExtension()
    {
        return this.extension;
    }
}

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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.officeimporter.converter.OfficeConverter;
import org.xwiki.officeimporter.converter.OfficeConverterException;
import org.xwiki.officeimporter.converter.OfficeConverterResult;
import org.xwiki.officeimporter.converter.OfficeDocumentFormat;

import com.xwiki.collabora.configuration.CollaboraConfiguration;

import static com.xwiki.collabora.internal.CollaboraOfficeConverterFormatHelper.DEFAULT_OUTPUT_FORMAT;
import static com.xwiki.collabora.internal.CollaboraOfficeConverterFormatHelper.SUPPORTED_INPUT_FORMATS_IMPRESS;

/**
 * An implementation of the {@link OfficeConverter} which relies on a Collabora Server to do the conversion.
 *
 * @version $Id$
 * @since 1.6.0
 */
public class CollaboraOfficeConverter implements OfficeConverter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CollaboraOfficeConverter.class);

    private final CollaboraConfiguration configuration;

    /**
     * Create a new converter.
     *
     * @param configuration the collabora configuration.
     */
    public CollaboraOfficeConverter(CollaboraConfiguration configuration)
    {
        this.configuration = configuration;
    }

    @Override
    public OfficeConverterResult convertDocument(Map<String, InputStream> inputStreams, String inputFileName,
        String outputFileName) throws OfficeConverterException
    {
        LOGGER.debug("Starting conversion of file [{}], to [{}]", inputFileName, outputFileName);

        if (!this.configuration.isEnabled()) {
            throw new OfficeConverterException("Collabora server is not enabled");
        }

        if (inputStreams.entrySet().size() != 1) {
            throw new OfficeConverterException("Collabora office converter only supports one input file");
        }

        InputStream fileBody = inputStreams.entrySet().iterator().next().getValue();

        String inputFormat = FilenameUtils.getExtension(inputFileName);
        String outputFormat = FilenameUtils.getExtension(outputFileName);
        outputFormat = (StringUtils.isNotBlank(outputFormat)) ? outputFormat : DEFAULT_OUTPUT_FORMAT;

        LOGGER.debug("Found file extensions [{}] (input) and [{}] (output)", inputFormat, outputFormat);

        // Ensure that the input and output formats are compatible
        if (!CollaboraOfficeConverterFormatHelper.isConversionSupportedForFormat(inputFormat, outputFormat)) {
            throw new OfficeConverterException(String.format("Conversion from format [%s] to [%s] is not supported by"
                    + " the Collabora server.", inputFormat, outputFormat));
        }

        return convertInternal(fileBody, inputFileName, outputFileName, outputFormat);
    }

    private CollaboraOfficeConverterResult convertInternal(InputStream fileBody, String inputFileName,
        String outputFileName, String outputFormat) throws OfficeConverterException
    {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String conversionURL = String.format("%s/cool/convert-to/%s",
                this.configuration.getServerURL(), outputFormat);

            LOGGER.debug("Making a request to conversion URL [{}]", conversionURL);

            HttpPost post = new HttpPost(conversionURL);
            HttpEntity entity = MultipartEntityBuilder.create().addPart("file",
                new InputStreamBody(fileBody, inputFileName)).build();
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                return handleResponse(response, outputFileName);
            }
        } catch (IOException e) {
            throw new OfficeConverterException("Failed to make a request to Collabora", e);
        }
    }

    private CollaboraOfficeConverterResult handleResponse(CloseableHttpResponse response, String outputFileName)
        throws OfficeConverterException, IOException
    {
        int statusCode = response.getStatusLine().getStatusCode();
        LOGGER.debug("Got response code [{}]", statusCode);

        if (statusCode != 200) {
            throw new OfficeConverterException(
                String.format("Found invalid return code [%s] when requesting conversion to Collabora",
                    statusCode));
        } else {
            Path directory = Files.createTempDirectory("collabora-office-importer");
            File outputFile = new File(directory.toFile(), outputFileName);
            LOGGER.debug("Storing conversion result to [{}]", outputFile);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                response.getEntity().writeTo(fos);
            }

            return new CollaboraOfficeConverterResult(outputFile);
        }
    }

    @Override
    public boolean isPresentation(String officeFileName)
    {
        return SUPPORTED_INPUT_FORMATS_IMPRESS.contains(FilenameUtils.getExtension(officeFileName));
    }

    @Override
    public OfficeDocumentFormat getDocumentFormat(String officeFileName)
    {
        // This method can be called in the case where documents are exported using XWiki's client-side PDF export
        // (XWiki 14.2+).
        // In this case, the target page for the export is called with the export action, and the query parameter
        // "format=html-print". As of XWiki 17.4.4, this format will be checked against
        // OfficeConverter#getDocumentFormat(), which is expected to return null.
        if ("html-print".equals(officeFileName)) {
            return null;
        // Some parts of XWiki Platform will provide this API directly with a file extension instead of a full file
        // name.
        // See ExportAction.java#L163 / OfficeExporter.java#L71
        } else if (officeFileName.indexOf('.') == -1) {
            return new CollaboraOfficeDocumentFormat(String.format("my_file.%s", officeFileName));
        } else {
            return new CollaboraOfficeDocumentFormat(officeFileName);
        }
    }

    @Override
    public boolean isMediaTypeSupported(String mediaType)
    {
        MediaType mt = MediaType.parse(mediaType);
        return MediaTypeRegistry.getDefaultRegistry().getTypes().contains(mt);
    }

    @Override
    public boolean isConversionSupported(String inputMediaType, String outputMediaType)
    {
        return CollaboraOfficeConverterFormatHelper.isConversionSupportedForMediaType(inputMediaType, outputMediaType);
    }
}


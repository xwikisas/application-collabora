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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

/**
 * An implementation of the {@link OfficeConverter} which relies on a Collabora Server to do the conversion.
 *
 * @version $Id$
 * @since 1.6
 */
public class CollaboraOfficeConverter implements OfficeConverter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(CollaboraOfficeConverter.class);

    private static final String FORMAT_ODT = "odt";

    // Supported format extracted from this list : https://www.collaboraonline.com/document-conversion/
    private static final List<String> INPUT_FORMATS_WRITER_DOCS = List.of("sxw", FORMAT_ODT, "fodt");

    private static final List<String> INPUT_FORMATS_CALC_DOCS = List.of("sxc", "ods", "fods");

    private static final List<String> INPUT_FORMATS_IMPRESS_DOCS = List.of("sxi", "odp", "fodp");

    private static final List<String> INPUT_FORMATS_DRAW_DOCS = List.of("sxd", "odg", "fodg");

    private static final List<String> INPUT_FORMATS_CHART_DOCS = List.of("odc");

    private static final List<String> INPUT_FORMATS_TEXT_MASTER_DOCS = List.of("sxg", "odm");

    private static final List<String> INPUT_FORMATS_TEXT_TEMPLATE_DOCS = List.of("stw", "ott");

    private static final List<String> INPUT_FORMATS_WRITER_MASTER_DOC_TEMPLATES = List.of("otm");

    private static final List<String> INPUT_FORMATS_SPREADSHEET_TEMPLATE_DOCS = List.of("stc", "ots");

    private static final List<String> INPUT_FORMATS_PRESENTATION_TEMPLATE_DOCS = List.of("sti", "otp");

    private static final List<String> INPUT_FORMATS_DRAWING_TEMPLATE_DOCS = List.of("std", "otg");

    private static final List<String> INPUT_FORMATS_BASE_DOCS = List.of("odb");

    private static final List<String> INPUT_FORMATS_EXTENSIONS = List.of("oxt");

    private static final List<String> INPUT_FORMATS_MS_WORD = List.of("doc", "dot");

    private static final List<String> INPUT_FORMATS_MS_EXCEL = List.of("xls");

    private static final List<String> INPUT_FORMATS_MS_POWERPOINT = List.of("ppt");

    private static final List<String> INPUT_FORMATS_OOXML_WORDPROCESSING = List.of("docx", "docm", "dotx", "dotm");

    private static final List<String> INPUT_FORMATS_OOXML_SPREADSHEET = List.of("xltx", "xltm", "xlsx", "xlsb", "xlsm");

    private static final List<String> INPUT_FORMATS_OOXML_PRESENTATION = List.of("pptx", "pptm", "potx", "potm");

    private static final List<String> INPUT_FORMATS_OTHER1 = List.of("wpd", "pdb", "hwp", "wps", "wri", "wk1", "cgm",
        "dxf", "emf", "wmf", "cdr", "vsd", "pub", "vss", "lrf", "gnumeric", "mw", "numbers", "p65", "pdf", "jpg",
        "jpeg", "gif", "png", "etc");

    private static final List<String> INPUT_FORMATS_OTHER2 = List.of("dif", "slk", "csv", "dbf", "oth", "rtf", "txt");

    private static final List<String> SUPPORTED_INPUT_FORMATS_WRITER = Stream.of(
        INPUT_FORMATS_WRITER_DOCS, INPUT_FORMATS_TEXT_MASTER_DOCS, INPUT_FORMATS_TEXT_TEMPLATE_DOCS,
        INPUT_FORMATS_WRITER_MASTER_DOC_TEMPLATES, INPUT_FORMATS_MS_WORD, INPUT_FORMATS_OOXML_WORDPROCESSING)
        .flatMap(Collection::stream).collect(Collectors.toList());

    private static final List<String> SUPPORTED_INPUT_FORMATS_CALC = Stream.of(
        INPUT_FORMATS_CALC_DOCS, INPUT_FORMATS_SPREADSHEET_TEMPLATE_DOCS, INPUT_FORMATS_MS_EXCEL,
        INPUT_FORMATS_OOXML_SPREADSHEET).flatMap(Collection::stream).collect(Collectors.toList());

    private static final List<String> SUPPORTED_INPUT_FORMATS_IMPRESS = Stream.of(
        INPUT_FORMATS_IMPRESS_DOCS, INPUT_FORMATS_PRESENTATION_TEMPLATE_DOCS, INPUT_FORMATS_MS_POWERPOINT,
        INPUT_FORMATS_OOXML_PRESENTATION).flatMap(Collection::stream).collect(Collectors.toList());

    private static final List<String> SUPPORTED_INPUT_FORMATS_OTHER = Stream.of(
        INPUT_FORMATS_DRAW_DOCS, INPUT_FORMATS_CHART_DOCS, INPUT_FORMATS_DRAWING_TEMPLATE_DOCS, INPUT_FORMATS_BASE_DOCS,
        INPUT_FORMATS_EXTENSIONS, INPUT_FORMATS_OTHER1, INPUT_FORMATS_OTHER2).flatMap(Collection::stream)
        .collect(Collectors.toList());

    private static final List<String> SUPPORTED_INPUT_FORMATS_ALL = Stream.of(
        SUPPORTED_INPUT_FORMATS_WRITER, SUPPORTED_INPUT_FORMATS_CALC, SUPPORTED_INPUT_FORMATS_IMPRESS,
        SUPPORTED_INPUT_FORMATS_OTHER).flatMap(Collection::stream).collect(Collectors.toList());

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

        // TODO: check if the input file format is acceptable before sending it to LO
        String inputFormat = FilenameUtils.getExtension(inputFileName);
        String outputFormat = FilenameUtils.getExtension(outputFileName);
        outputFormat = (StringUtils.isNotBlank(outputFormat)) ? outputFormat : FORMAT_ODT;

        LOGGER.debug("Found file extensions [{}] (input) and [{}] (output)", inputFormat, outputFormat);

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
        // Some parts of XWiki Platform will provide this API directly with a file extension instead of a full file
        // name.
        // See ExportAction.java#L163 / OfficeExporter.java#L71
        if (officeFileName.indexOf('.') == -1) {
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
        // TODO: Find a better way
        return true;
    }
}


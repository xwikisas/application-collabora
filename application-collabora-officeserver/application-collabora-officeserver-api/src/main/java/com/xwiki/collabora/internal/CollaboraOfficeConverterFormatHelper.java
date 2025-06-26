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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.tika.Tika;

/**
 * Helper to define if input / output formats are supported for conversion by the Collabora Office.
 *
 * @version $Id$
 * @since 1.6.0
 */
public final class CollaboraOfficeConverterFormatHelper
{
    // Supported format extracted from this list: https://www.collaboraonline.com/document-conversion/

    private static final Tika TIKA = new Tika();

    // This list defines all the document formats defined on the page above, in the order as they appear on the page
    // to help with maintainability.
    private static final String FORMAT_SXW = "sxw";

    private static final String FORMAT_ODT = "odt";

    private static final String FORMAT_FODT = "fodt";

    private static final Set<String> INPUT_FORMATS_WRITER_DOCS = Set.of(FORMAT_SXW, FORMAT_ODT, FORMAT_FODT);

    private static final String FORMAT_SXC = "sxc";

    private static final String FORMAT_ODS = "ods";

    private static final String FORMAT_FODS = "fods";

    private static final Set<String> INPUT_FORMATS_CALC_DOCS = Set.of(FORMAT_SXC, FORMAT_ODS, FORMAT_FODS);

    private static final String FORMAT_SXI = "sxi";

    private static final String FORMAT_ODP = "odp";

    private static final String FORMAT_FODP = "fodp";

    private static final Set<String> INPUT_FORMATS_IMPRESS_DOCS = Set.of(FORMAT_SXI, FORMAT_ODP, FORMAT_FODP);

    private static final String FORMAT_SXD = "sxd";

    private static final String FORMAT_ODG = "odg";

    private static final String FORMAT_FODG = "fodg";

    private static final Set<String> INPUT_FORMATS_DRAW_DOCS = Set.of(FORMAT_SXD, FORMAT_ODG, FORMAT_FODG);

    private static final String FORMAT_ODC = "odc";

    private static final Set<String> INPUT_FORMATS_CHART_DOCS = Set.of(FORMAT_ODC);

    private static final String FORMAT_SXG = "sxg";

    private static final String FORMAT_ODM = "odm";

    private static final Set<String> INPUT_FORMATS_TEXT_MASTER_DOCS = Set.of(FORMAT_SXG, FORMAT_ODM);

    private static final String FORMAT_STW = "stw";

    private static final String FORMAT_OTT = "ott";

    private static final Set<String> INPUT_FORMATS_TEXT_TEMPLATE_DOCS = Set.of(FORMAT_STW, FORMAT_OTT);

    private static final String FORMAT_OTM = "otm";

    private static final Set<String> INPUT_FORMATS_WRITER_MASTER_DOC_TEMPLATES = Set.of(FORMAT_OTM);

    private static final String FORMAT_STC = "stc";

    private static final String FORMAT_OTS = "ots";

    private static final Set<String> INPUT_FORMATS_SPREADSHEET_TEMPLATE_DOCS = Set.of(FORMAT_STC, FORMAT_OTS);

    private static final String FORMAT_STI = "sti";

    private static final String FORMAT_OTP = "otp";

    private static final Set<String> INPUT_FORMATS_PRESENTATION_TEMPLATE_DOCS = Set.of(FORMAT_STI, FORMAT_OTP);

    private static final String FORMAT_STD = "std";

    private static final String FORMAT_OTG = "otg";

    private static final Set<String> INPUT_FORMATS_DRAWING_TEMPLATE_DOCS = Set.of(FORMAT_STD, FORMAT_OTG);

    private static final String FORMAT_ODB = "odb";

    private static final Set<String> INPUT_FORMATS_BASE_DOCS = Set.of(FORMAT_ODB);

    private static final String FORMAT_OXT = "oxt";

    private static final Set<String> INPUT_FORMATS_EXTENSIONS = Set.of(FORMAT_OXT);

    private static final String FORMAT_DOC = "doc";

    private static final String FORMAT_DOT = "dot";

    private static final Set<String> INPUT_FORMATS_MS_WORD = Set.of(FORMAT_DOC, FORMAT_DOT);

    private static final String FORMAT_XLS = "xls";

    private static final Set<String> INPUT_FORMATS_MS_EXCEL = Set.of(FORMAT_XLS);

    private static final String FORMAT_PPT = "ppt";

    private static final Set<String> INPUT_FORMATS_MS_POWERPOINT = Set.of(FORMAT_PPT);

    private static final String FORMAT_DOCX = "docx";

    private static final String FORMAT_DOCM = "docm";

    private static final String FORMAT_DOTX = "dotx";

    private static final String FORMAT_DOTM = "dotm";

    private static final Set<String> INPUT_FORMATS_OOXML_WORDPROCESSING = Set.of(FORMAT_DOCX, FORMAT_DOCM,
        FORMAT_DOTX, FORMAT_DOTM);

    private static final String FORMAT_XLTX = "xltx";

    private static final String FORMAT_XLTM = "xltm";

    private static final String FORMAT_XLSX = "xlsx";

    private static final String FORMAT_XLSB = "xlsb";

    private static final String FORMAT_XLSM = "xlsm";

    private static final Set<String> INPUT_FORMATS_OOXML_SPREADSHEET = Set.of(FORMAT_XLTX, FORMAT_XLTM, FORMAT_XLSX,
        FORMAT_XLSB, FORMAT_XLSM);

    private static final String FORMAT_PPTX = "pptx";

    private static final String FORMAT_PPTM = "pptm";

    private static final String FORMAT_POTX = "potx";

    private static final String FORMAT_POTM = "potm";

    private static final Set<String> INPUT_FORMATS_OOXML_PRESENTATION = Set.of(FORMAT_PPTX, FORMAT_PPTM,
        FORMAT_POTX, FORMAT_POTM);

    private static final String FORMAT_WPD = "wpd";

    private static final String FORMAT_PDB = "pdb";

    private static final String FORMAT_HWP = "hwp";

    private static final String FORMAT_WPS = "wps";

    private static final String FORMAT_WRI = "wri";

    private static final String FORMAT_WK1 = "wk1";

    private static final String FORMAT_CGM = "cgm";

    private static final String FORMAT_DXF = "dxf";

    private static final String FORMAT_EMF = "emf";

    private static final String FORMAT_WMF = "wmf";

    private static final String FORMAT_CDR = "cdr";

    private static final String FORMAT_VSD = "vsd";

    private static final String FORMAT_PUB = "pub";

    private static final String FORMAT_VSS = "vss";

    private static final String FORMAT_LRF = "lrf";

    private static final String FORMAT_GNUMERIC = "gnumeric";

    private static final String FORMAT_MW = "mw";

    private static final String FORMAT_NUMBERS = "numbers";

    private static final String FORMAT_P65 = "p65";

    private static final String FORMAT_PDF = "pdf";

    private static final String FORMAT_JPG = "jpg";

    private static final String FORMAT_JPEG = "jpeg";

    private static final String FORMAT_GIF = "gif";

    private static final String FORMAT_PNG = "png";

    private static final String FORMAT_ETC = "etc";

    private static final Set<String> INPUT_FORMATS_OTHER1 = Set.of(FORMAT_WPD, FORMAT_PDB, FORMAT_HWP,
        FORMAT_WPS, FORMAT_WRI, FORMAT_WK1, FORMAT_CGM, FORMAT_DXF, FORMAT_EMF, FORMAT_WMF, FORMAT_CDR, FORMAT_VSD,
        FORMAT_PUB, FORMAT_VSS, FORMAT_LRF, FORMAT_GNUMERIC, FORMAT_MW, FORMAT_NUMBERS, FORMAT_P65, FORMAT_PDF,
        FORMAT_JPG, FORMAT_JPEG, FORMAT_GIF, FORMAT_PNG, FORMAT_ETC);

    private static final String FORMAT_DIF = "dif";

    private static final String FORMAT_SLK = "slk";

    private static final String FORMAT_CSV = "csv";

    private static final String FORMAT_DBF = "dbf";

    private static final String FORMAT_OTH = "oth";

    private static final String FORMAT_RTF = "rtf";

    private static final String FORMAT_TXT = "txt";

    private static final Set<String> INPUT_FORMATS_OTHER2 = Set.of(FORMAT_DIF, FORMAT_SLK, FORMAT_CSV, FORMAT_DBF,
        FORMAT_OTH, FORMAT_RTF, FORMAT_TXT);

    // The following formats are not supported as an input in the official documentation. We add them here because
    // they are supported output formats.

    private static final String FORMAT_HTML = "html";

    private static final String FORMAT_XHTML = "xhtml";

    private static final String FORMAT_POT = "pot";

    private static final String FORMAT_PPS = "pps";

    private static final String FORMAT_SVG = "svg";

    private static final String FORMAT_SWF = "swf";

    // Expose input formats and media types that are supported

    // We explicitly add HTML and XHTML formats to the supported writer input formats to enable the
    // possibility to perform document imports.
    // These formats are, however, not listed in the official documentation.

    /**
     * Input formats supported for Writer.
     */
    public static final Set<String> SUPPORTED_INPUT_FORMATS_WRITER = Stream.of(
            INPUT_FORMATS_WRITER_DOCS, INPUT_FORMATS_TEXT_MASTER_DOCS, INPUT_FORMATS_TEXT_TEMPLATE_DOCS,
            INPUT_FORMATS_WRITER_MASTER_DOC_TEMPLATES, INPUT_FORMATS_MS_WORD, INPUT_FORMATS_OOXML_WORDPROCESSING,
            Set.of(FORMAT_HTML, FORMAT_XHTML))
        .flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());

    /**
     * Input media types supported for Writer.
     */
    public static final Set<String> SUPPORTED_INPUT_MEDIA_TYPES_WRITER =
        SUPPORTED_INPUT_FORMATS_WRITER.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    /**
     * Input formats supported for Calc.
     */
    public static final Set<String> SUPPORTED_INPUT_FORMATS_CALC = Stream.of(
        INPUT_FORMATS_CALC_DOCS, INPUT_FORMATS_SPREADSHEET_TEMPLATE_DOCS, INPUT_FORMATS_MS_EXCEL,
        INPUT_FORMATS_OOXML_SPREADSHEET).flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());

    /**
     * Input media types supported for Calc.
     */
    public static final Set<String> SUPPORTED_INPUT_MEDIA_TYPES_CALC =
        SUPPORTED_INPUT_FORMATS_CALC.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    /**
     * Input formats supported for Impress.
     */
    public static final Set<String> SUPPORTED_INPUT_FORMATS_IMPRESS = Stream.of(
        INPUT_FORMATS_IMPRESS_DOCS, INPUT_FORMATS_PRESENTATION_TEMPLATE_DOCS, INPUT_FORMATS_MS_POWERPOINT,
        INPUT_FORMATS_OOXML_PRESENTATION).flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());

    /**
     * Input media types supported for Impress.
     */
    public static final Set<String> SUPPORTED_INPUT_MEDIA_TYPES_IMPRESS =
        SUPPORTED_INPUT_FORMATS_IMPRESS.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    /**
     * Input formats supported for … others.
     */
    public static final Set<String> SUPPORTED_INPUT_FORMATS_OTHER = Stream.of(
            INPUT_FORMATS_DRAW_DOCS, INPUT_FORMATS_CHART_DOCS, INPUT_FORMATS_DRAWING_TEMPLATE_DOCS,
            INPUT_FORMATS_BASE_DOCS, INPUT_FORMATS_EXTENSIONS, INPUT_FORMATS_OTHER1, INPUT_FORMATS_OTHER2)
        .flatMap(Collection::stream).collect(Collectors.toUnmodifiableSet());

    /**
     * Other supported input media types.
     */
    public static final Set<String> SUPPORTED_INPUT_MEDIA_TYPES_OTHER =
        SUPPORTED_INPUT_FORMATS_OTHER.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    // Expose the output formats and media types that are supported

    /**
     * Output formats supported for Writer.
     */
    private static final Set<String> SUPPORTED_OUTPUT_FORMATS_WRITER = Set.of(FORMAT_DOC, FORMAT_DOCM, FORMAT_DOCX,
        FORMAT_FODT, FORMAT_HTML, FORMAT_ODT, FORMAT_OTT, FORMAT_PDF, FORMAT_RTF, FORMAT_TXT, FORMAT_XHTML, FORMAT_PNG);

    /**
     * Output media types supported for Writer.
     */
    public static final Set<String> SUPPORTED_OUTPUT_MEDIA_TYPES_WRITER =
        SUPPORTED_OUTPUT_FORMATS_WRITER.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    /**
     * Output formats supported for Calc.
     */
    private static final Set<String> SUPPORTED_OUTPUT_FORMATS_CALC = Set.of(FORMAT_HTML, FORMAT_ODS, FORMAT_OTS,
        FORMAT_PDF, FORMAT_XHTML, FORMAT_XLS, FORMAT_XLSM, FORMAT_XLSX, FORMAT_PNG);

    /**
     * Output media types supported for Calc.
     */
    public static final Set<String> SUPPORTED_OUTPUT_MEDIA_TYPES_CALC =
        SUPPORTED_OUTPUT_FORMATS_CALC.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    /**
     * Output formats supported for Impress.
     */
    public static final Set<String> SUPPORTED_OUTPUT_FORMATS_IMPRESS = Set.of(FORMAT_FODP, FORMAT_HTML, FORMAT_ODG,
        FORMAT_ODP, FORMAT_OTP, FORMAT_PDF, FORMAT_POTM, FORMAT_POT, FORMAT_PPTM, FORMAT_PPTX, FORMAT_PPS, FORMAT_PPT,
        FORMAT_SVG, FORMAT_SWF, FORMAT_XHTML, FORMAT_PNG);

    /**
     * Output media types supported for Impress.
     */
    public static final Set<String> SUPPORTED_OUTPUT_MEDIA_TYPES_IMPRESS =
        SUPPORTED_OUTPUT_FORMATS_IMPRESS.stream().map(TIKA::detect).collect(Collectors.toUnmodifiableSet());

    /**
     * Default output format, to be used in case no document format is provided.
     */
    public static final String DEFAULT_OUTPUT_FORMAT = FORMAT_ODT;

    private CollaboraOfficeConverterFormatHelper()
    {

    }

    /**
     * Indicates if conversion from one media type to another is supported by Collabora.
     *
     * @param inputMediaType the input media type
     * @param outputMediaType the output media type
     * @return true if the conversion is supported
     */
    public static boolean isConversionSupportedForMediaType(String inputMediaType, String outputMediaType)
    {
        if (SUPPORTED_INPUT_MEDIA_TYPES_WRITER.contains(inputMediaType)) {
            return SUPPORTED_OUTPUT_MEDIA_TYPES_WRITER.contains(outputMediaType);
        } else if (SUPPORTED_INPUT_MEDIA_TYPES_CALC.contains(inputMediaType)) {
            return SUPPORTED_OUTPUT_MEDIA_TYPES_CALC.contains(outputMediaType);
        } else if (SUPPORTED_INPUT_MEDIA_TYPES_IMPRESS.contains(inputMediaType)) {
            return SUPPORTED_OUTPUT_MEDIA_TYPES_IMPRESS.contains(outputMediaType);
        } else {
            return false;
        }
    }

    /**
     * Indicates if conversion from one input format to another is supported by Collabora.
     *
     * @param inputFormat the input media type
     * @param outputFormat the output media type
     * @return true if the conversion is supported
     */
    public static boolean isConversionSupportedForFormat(String inputFormat, String outputFormat)
    {
        String lowerCaseInputFormat = inputFormat.toLowerCase();
        String lowerCaseOutputFormat = outputFormat.toLowerCase();

        if (SUPPORTED_INPUT_FORMATS_WRITER.contains(lowerCaseInputFormat)) {
            return SUPPORTED_OUTPUT_FORMATS_WRITER.contains(lowerCaseOutputFormat);
        } else if (SUPPORTED_INPUT_FORMATS_CALC.contains(lowerCaseInputFormat)) {
            return SUPPORTED_OUTPUT_FORMATS_CALC.contains(lowerCaseOutputFormat);
        } else if (SUPPORTED_INPUT_FORMATS_IMPRESS.contains(lowerCaseInputFormat)) {
            return SUPPORTED_OUTPUT_FORMATS_IMPRESS.contains(lowerCaseOutputFormat);
        } else {
            return false;
        }
    }
}

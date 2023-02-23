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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;

/**
 * To set up the iframe, the WOPI host (the application) needs to read a discovery XML from a defined location on the
 * WOPI client (the Collabora Online server). The discovery is available at:
 * https://<WOPIClientURL>:<port>/hosting/discovery. The reply is discovery.xml that contains urlsrc for various file
 * formats. The urlsrc needs to be used in the iframe for editing the document.
 *
 * @version $Id$
 * @since 1.0
 */
@Component(roles = DiscoveryManager.class)
@Singleton
public class DiscoveryManager
{
    @Inject
    @Named("collabora")
    private Provider<ConfigurationSource> configuration;

    /**
     * Get the urlSrc specific to this type of file. This is needed in order to know which part of Collabora online to
     * load.
     *
     * @param fileId id of the file
     * @return the urlSrc specific to this file format
     * @throws IOException If an error occurred while getting the information from the Collabora server
     */
    public String getURLSrc(String fileId) throws IOException
    {
        URL collaboraDiscovery = new URL(this.configuration.get().getProperty("server") + "/hosting/discovery");
        HttpURLConnection connection = (HttpURLConnection) collaboraDiscovery.openConnection();
        connection.setRequestMethod("GET");

        return getURLSrc(getConnectionResponse(connection), fileId);
    }

    private static String getConnectionResponse(HttpURLConnection connection) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    private String getURLSrc(String response, String fileId)
    {
        String ext = fileId.substring(fileId.lastIndexOf(".") + 1);
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(response.getBytes()));

            NodeList nodeList = document.getElementsByTagName("action");
            for (int i = 0; i <= nodeList.getLength(); i++) {
                Element elem = (Element) nodeList.item(i);
                if (elem.getAttribute("ext").equals(ext)) {
                    return elem.getAttribute("urlsrc");
                }
            }

            return null;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}

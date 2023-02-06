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
package com.xwiki.collabora.internal.rest;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xwiki.component.annotation.Component;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.rest.internal.resources.pages.ModifiablePageResource;

import com.xpn.xwiki.XWikiContext;
import com.xwiki.collabora.internal.FileToken;
import com.xwiki.collabora.internal.FileTokenManager;
import com.xwiki.collabora.rest.Discovery;

/**
 * Default implementation of {@link Discovery}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("com.xwiki.collabora.internal.rest.DefaultDiscovery")
@Singleton
public class DefaultDiscovery extends ModifiablePageResource implements Discovery
{
    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private FileTokenManager fileTokenManager;

    @Override
    public Response getDiscovery(String server, String ext, String fileId) throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        try {
            URL collaboraDiscovery = new URL(server + "/hosting/discovery");
            HttpURLConnection connection = (HttpURLConnection) collaboraDiscovery.openConnection();
            connection.setRequestMethod("GET");

            String urlSrc = getURLSrc(getConnectionResponse(connection), ext);

            JSONObject message = new JSONObject();
            message.put("urlSrc", urlSrc);
            FileToken token = fileTokenManager.getToken(xcontext.getUserReference().toString(), fileId);
            message.put("token", token.toString());

            return Response.status(Response.Status.OK).entity(message.toString()).type(MediaType.APPLICATION_JSON)
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Response clearToken(String fileId) throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        this.fileTokenManager.clearToken(xcontext.getUserReference().toString(), fileId);
        return Response.ok().build();
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

    private String getURLSrc(String response, String ext)
    {
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

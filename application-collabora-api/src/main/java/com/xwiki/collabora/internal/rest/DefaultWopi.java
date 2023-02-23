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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.AttachmentReferenceResolver;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.rest.internal.resources.pages.ModifiablePageResource;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xwiki.collabora.internal.AttachmentManager;
import com.xwiki.collabora.internal.DiscoveryManager;
import com.xwiki.collabora.internal.FileTokenManager;
import com.xwiki.collabora.rest.Wopi;
import com.xwiki.collabora.rest.model.jaxb.ObjectFactory;
import com.xwiki.collabora.rest.model.jaxb.Token;

/**
 * Default implementation of {@link Wopi}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named("com.xwiki.collabora.internal.rest.DefaultWopi")
@Singleton
public class DefaultWopi extends ModifiablePageResource implements Wopi
{
    private static final String LAST_MODIFIED_TIME = "LastModifiedTime";

    // Collabora server needs time in ISO8601 round-trip time format, to include fractional seconds.
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private FileTokenManager fileTokenManager;

    @Inject
    private DiscoveryManager discoveryManager;

    @Inject
    private AttachmentManager attachmentManager;

    @Inject
    @Named("current")
    private AttachmentReferenceResolver<String> attachmentReferenceResolver;

    @Override
    public Response get(String fileId, String token, String userCanWrite) throws XWikiRestException
    {
        if (token == null || fileTokenManager.isInvalid(token)) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);
        try {
            XWikiAttachment attachment = attachmentManager.getAttachment(attachmentReference);
            JSONObject message = new JSONObject();
            message.put("BaseFileName", attachmentReference.getName());
            message.put("Size", String.valueOf(attachment.getLongSize()));
            message.put("UserCanWrite", userCanWrite);
            message.put(LAST_MODIFIED_TIME, dateFormat.format(attachment.getDate()));

            return Response.status(Response.Status.OK).entity(message.toString()).type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Response getContents(String fileId, String token) throws XWikiRestException
    {
        if (fileTokenManager.isInvalid(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        XWikiContext xcontext = this.contextProvider.get();
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);
        try {
            XWikiAttachment attachment = attachmentManager.getAttachment(attachmentReference);

            return Response.ok().entity(attachment.getContentInputStream(xcontext)).type(attachment.getMimeType())
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }

    @Override
    public Response postContents(String fileId, String token, byte[] body) throws XWikiRestException
    {
        if (fileTokenManager.isInvalid(token)) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        XWikiAttachment attachment =
            attachmentManager.createOrUpdateAttachment(this.attachmentReferenceResolver.resolve(fileId), body);

        JSONObject response = new JSONObject();
        response.put(LAST_MODIFIED_TIME, dateFormat.format(attachment.getDate()));

        return Response.status(Response.Status.OK).entity(response.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Token getToken(String fileId) throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        try {
            String urlSrc = discoveryManager.getURLSrc(fileId);

            Token token = (new ObjectFactory()).createToken();
            token.setUrlSrc(urlSrc);
            token.setValue(fileTokenManager.getToken(xcontext.getUserReference().toString(), fileId).toString());

            return token;
        } catch (IOException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @Override
    public Token clearToken(String fileId) throws XWikiRestException
    {
        int tokenUsage =
            this.fileTokenManager.clearToken(this.contextProvider.get().getUserReference().toString(), fileId);

        Token token = (new ObjectFactory()).createToken();
        token.setUsage(tokenUsage);

        return token;
    }
}

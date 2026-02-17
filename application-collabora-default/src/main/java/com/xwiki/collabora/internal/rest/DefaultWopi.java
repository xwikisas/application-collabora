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
import java.util.Base64;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.AttachmentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.rest.internal.resources.pages.ModifiablePageResource;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.web.XWikiRequest;
import com.xwiki.collabora.internal.AttachmentManager;
import com.xwiki.collabora.internal.DiscoveryManager;
import com.xwiki.collabora.internal.FileTokenManager;
import com.xwiki.collabora.internal.UserManager;
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
    private Logger logger;

    @Inject
    @Named("current")
    private AttachmentReferenceResolver<String> attachmentReferenceResolver;

    @Inject
    private UserManager userManager;

    @Inject
    private EntityReferenceSerializer<String> referenceSerializer;

    @Override
    public Response get(String fileId, String token) throws XWikiRestException
    {
        String decodedToken = new String(Base64.getUrlDecoder().decode(token));
        String decodedFileId = new String(Base64.getUrlDecoder().decode(fileId));

        if (token == null || fileTokenManager.isInvalid(decodedToken) || !fileTokenManager.hasAccess(decodedToken)) {
            logger.warn("Failed to get file [{}] due to invalid token or restricted rights.", decodedFileId);
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(decodedFileId);
        try {
            XWikiAttachment attachment = attachmentManager.getAttachment(attachmentReference);
            JSONObject message = new JSONObject();
            message.put("BaseFileName", attachmentReference.getName());
            message.put("Size", String.valueOf(attachment.getLongSize()));
            message.put("UserCanWrite", fileTokenManager.hasWriteAccess(decodedToken));
            message.put("UserId",
                referenceSerializer.serialize(fileTokenManager.getTokenUserDocReference(decodedToken)));
            message.put("UserFriendlyName",
                userManager.getUserFriendlyName(fileTokenManager.getTokenUserDocReference(decodedToken)));
            message.put(LAST_MODIFIED_TIME, dateFormat.format(attachment.getDate()));
            // Needed for using the PostMessage API.
            XWikiRequest wikiRequest = contextProvider.get().getRequest();
            String postMessageOrigin = String.format("%s://%s:%s", wikiRequest.getScheme(), wikiRequest.getServerName(),
                wikiRequest.getServerPort());
            message.put("PostMessageOrigin", postMessageOrigin);

            return Response.status(Response.Status.OK).entity(message.toString()).type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.warn("Failed to get file [{}]. Root cause: [{}]", decodedFileId,
                ExceptionUtils.getRootCauseMessage(e));
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response getContents(String fileId, String token) throws XWikiRestException
    {
        String decodedToken = new String(Base64.getUrlDecoder().decode(token));
        String decodedFileId = new String(Base64.getUrlDecoder().decode(fileId));

        if (fileTokenManager.isInvalid(decodedToken) || !fileTokenManager.hasAccess(decodedToken)) {
            logger.warn("Failed to get content of file [{}] due to invalid token or restricted rights.", decodedFileId);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        XWikiContext xcontext = this.contextProvider.get();
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(decodedFileId);
        try {
            XWikiAttachment attachment = attachmentManager.getAttachment(attachmentReference);

            return Response.ok().entity(attachment.getContentInputStream(xcontext)).type(attachment.getMimeType())
                .build();
        } catch (Exception e) {
            logger.warn("Failed to get content of file [{}]. Root cause: [{}]", decodedFileId,
                ExceptionUtils.getRootCauseMessage(e));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).build();
        }
    }

    @Override
    public Response postContents(String fileId, String token, byte[] body) throws XWikiRestException
    {
        String decodedToken = new String(Base64.getUrlDecoder().decode(token));
        String decodedFileId = new String(Base64.getUrlDecoder().decode(fileId));

        if (fileTokenManager.isInvalid(decodedToken) || !fileTokenManager.hasAccess(decodedToken)) {
            logger.warn("Failed to update file [{}] due to invalid token or restricted rights.", decodedFileId);
            // As the cause of a failure in updating the content may be an expired token, we return 200 status code for
            // now since the UNAUTHORIZED message should be returned after trying first to extend the token.
            // For this, subsequently to this request, an attempt to extend the token validity is done and only if
            // this is not possible (e.g. due to insufficient rights) the correct UNAUTHORIZED message is returned.
            // This is needed since we couldn't find a way to renew the token before the save request done by Collabora.
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).build();
        }

        try {
            XWikiAttachment attachment =
                attachmentManager.createOrUpdateAttachment(this.attachmentReferenceResolver.resolve(decodedFileId),
                    body, fileTokenManager.getTokenUserDocReference(decodedToken));

            JSONObject response = new JSONObject();
            response.put(LAST_MODIFIED_TIME, dateFormat.format(attachment.getDate()));

            return Response.status(Response.Status.OK).entity(response.toString()).type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            logger.warn("Failed to update file [{}]. Root cause: [{}]", decodedFileId,
                ExceptionUtils.getRootCauseMessage(e));
            throw new XWikiRestException(e);
        }
    }

    @Override
    public Token getToken(String fileId, String mode) throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        // Make sure that the current wiki is used on the XWiki context, since the REST resource is rooted on the
        // main wiki.
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);
        xcontext.setWikiReference(attachmentReference.getDocumentReference().getWikiReference());

        try {
            String urlSrc = discoveryManager.getURLSrc(fileId);
            String fileTokenValue = fileTokenManager.getToken(xcontext.getUserReference(), fileId, mode).toString();

            Token token = (new ObjectFactory()).createToken();
            token.setUrlSrc(urlSrc);
            token.setValue(fileTokenValue);

            return token;
        } catch (IOException e) {
            logger.warn("Failed to create token for file [{}]. Root cause: [{}]", fileId,
                ExceptionUtils.getRootCauseMessage(e));
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Token clearToken(String fileId, String mode) throws XWikiRestException
    {
        int tokenUsage = this.fileTokenManager.clearToken(this.contextProvider.get().getUserReference(), fileId, mode);

        Token token = (new ObjectFactory()).createToken();
        token.setUsage(tokenUsage);

        return token;
    }

    @Override
    public Response updateTokenExpiration(String fileId, String mode) throws XWikiRestException
    {
        try {
            XWikiContext xcontext = this.contextProvider.get();
            if (fileTokenManager.isInvalid(fileId, xcontext.getUserReference())) {
                if (fileTokenManager.hasAccess(fileId, xcontext.getUserReference(), mode)) {
                    fileTokenManager.extendToken(fileId, xcontext.getUserReference());
                    return Response.ok().type(MediaType.TEXT_PLAIN_TYPE).build();
                } else {
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            }
            return Response.notModified().type(MediaType.TEXT_PLAIN_TYPE).build();
        } catch (Exception e) {
            logger.warn("Failed to update token expiration for file [{}]. Root cause: [{}]", fileId,
                ExceptionUtils.getRootCauseMessage(e));
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

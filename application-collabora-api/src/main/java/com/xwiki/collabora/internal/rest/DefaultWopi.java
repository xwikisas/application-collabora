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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.xwiki.component.annotation.Component;
import org.xwiki.localization.ContextualLocalizationManager;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.AttachmentReferenceResolver;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.rest.internal.resources.pages.ModifiablePageResource;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xwiki.collabora.rest.Wopi;

/**
 * Default implementation of {@link Wopi}.
 *
 * @version $Id:$
 * @since 1.0
 */
@Component
@Named("com.xwiki.collabora.internal.rest.DefaultWopi")
@Singleton
public class DefaultWopi extends ModifiablePageResource implements Wopi
{
    final private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    @Inject
    protected ContextualLocalizationManager contextualLocalizationManager;

    @Inject
    private Provider<XWikiContext> contextProvider;

    /**
     * Create attachment name from a string reference.
     */
    @Inject
    @Named("current")
    private AttachmentReferenceResolver<String> attachmentReferenceResolver;

    @Override
    public Response get(String fileId) throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);
        try {
            DocumentReference documentReference = (DocumentReference) attachmentReference.getParent();
            XWikiDocument doc = xcontext.getWiki().getDocument(documentReference, xcontext);
            XWikiAttachment attachment = doc.getAttachment(attachmentReference.getName());

            // Add LastModifiedTime. Check edit rights for UserCanWrite.
            JSONObject message = new JSONObject();
            message.put("BaseFileName", attachmentReference.getName());
            message.put("Size", String.valueOf(attachment.getLongSize()));
            message.put("UserCanWrite", "true");
            message.put("LastModifiedTime", df.format(attachment.getDate()));

            return Response.status(Response.Status.OK).entity(message.toString()).type(MediaType.APPLICATION_JSON)
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }

    @Override
    public Response getContents(String fileId) throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);

        try {
            DocumentReference documentReference = attachmentReference.getDocumentReference();
            XWikiDocument doc = xcontext.getWiki().getDocument(documentReference, xcontext);
            XWikiAttachment attachment = doc.getAttachment(attachmentReference.getName());
            return Response.ok().entity(attachment.getContentInputStream(xcontext)).type(attachment.getMimeType())
                .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }

    @Override
    public Response postContents(String fileId, byte[] body) throws XWikiRestException
    {
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);

        XWikiAttachment attachment = createOrUpdateAttachment(attachmentReference, body);

        JSONObject response = new JSONObject();
        response.put("LastModifiedTime", df.format(attachment.getDate()));

        return Response.status(Response.Status.OK).entity(response.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Response postRelativeContents(String fileId, byte[] body) throws XWikiRestException
    {
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);

        XWikiAttachment attachment = createOrUpdateAttachment(attachmentReference, body);

        JSONObject response = new JSONObject();
        response.put("LastModifiedTime", df.format(attachment.getDate()));

        return Response.status(Response.Status.OK).entity(response.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    private XWikiAttachment createOrUpdateAttachment(AttachmentReference attachmentReference, byte[] content)
        throws XWikiRestException
    {
        XWikiContext xcontext = this.contextProvider.get();
        XWiki xwiki = xcontext.getWiki();
        DocumentReference documentReference = attachmentReference.getDocumentReference();

        try {
            // We clone the document because we're going to modify it and we shouldn't modify the cached instance.
            XWikiDocument document = xwiki.getDocument(documentReference, xcontext).clone();
            XWikiAttachment attachment =
                document.setAttachment(attachmentReference.getName(), new ByteArrayInputStream(content), xcontext);
            attachment.setAuthorReference(xcontext.getUserReference());

            document.setAuthorReference(xcontext.getUserReference());
            xwiki.saveDocument(document,
                this.contextualLocalizationManager.getTranslationPlain("collabora.save.comment"), xcontext);

            return attachment;
        } catch (XWikiException | IOException e) {
            throw new XWikiRestException(
                String.format("Failed to create or update the attachment [%s].", attachmentReference), e);
        }
    }
}

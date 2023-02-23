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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.localization.ContextualLocalizationManager;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rest.XWikiRestException;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * Manage document attachments.
 *
 * @version $Id$
 * @since 1.0
 */
@Component(roles = AttachmentManager.class)
@Singleton
public class AttachmentManager
{
    @Inject
    private ContextualLocalizationManager contextualLocalizationManager;

    @Inject
    private Provider<XWikiContext> contextProvider;

    /**
     * Create or update attachment with given content.
     *
     * @param attachmentReference reference of the attachment
     * @param content the new attachment content
     * @param userReference the current user reference
     * @return a modified {@link XWikiAttachment}
     * @throws XWikiRestException If an exception occurs while the Attachment is created or updated.
     */
    public XWikiAttachment createOrUpdateAttachment(AttachmentReference attachmentReference, byte[] content,
        DocumentReference userReference) throws XWikiException
    {
        XWikiContext xcontext = this.contextProvider.get();
        XWiki xwiki = xcontext.getWiki();
        DocumentReference documentReference = attachmentReference.getDocumentReference();

        try {
            // We clone the document because we're going to modify it and we shouldn't modify the cached instance.
            XWikiDocument document = xwiki.getDocument(documentReference, xcontext).clone();
            XWikiAttachment attachment =
                document.setAttachment(attachmentReference.getName(), new ByteArrayInputStream(content), xcontext);
            attachment.setAuthorReference(userReference);

            document.setAuthorReference(xcontext.getUserReference());
            xwiki.saveDocument(document,
                this.contextualLocalizationManager.getTranslationPlain("collabora.save.comment"), xcontext);

            return attachment;
        } catch (XWikiException | IOException e) {
            throw new XWikiException(
                String.format("Failed to create or update the attachment [%s].", attachmentReference), e);
        }
    }

    /**
     * @param attachmentReference reference of the attachment
     * @return the {@link XWikiAttachment} object corresponding to the given reference
     * @throws XWikiException If an exception occurs while accessing the attachment.
     */
    public XWikiAttachment getAttachment(AttachmentReference attachmentReference) throws XWikiException
    {
        XWikiContext xcontext = this.contextProvider.get();
        DocumentReference documentReference = attachmentReference.getDocumentReference();
        XWikiDocument doc = xcontext.getWiki().getDocument(documentReference, xcontext);

        return doc.getAttachment(attachmentReference.getName());
    }
}

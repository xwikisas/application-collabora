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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.AttachmentReferenceResolver;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.security.authorization.AuthorizationManager;
import org.xwiki.security.authorization.Right;

import com.xwiki.collabora.configuration.CollaboraConfiguration;

/**
 * Manage existing {@link FileToken} instances.
 *
 * @version $Id$
 * @since 1.0
 */
@Component(roles = FileTokenManager.class)
@Singleton
public class FileTokenManager
{
    private static final String XWIKI_GUEST = "XWiki.XWikiGuest";

    private static final String VIEW_MODE_KEY = "view";

    @Inject
    private Logger logger;

    @Inject
    private EntityReferenceSerializer<String> referenceSerializer;

    @Inject
    @Named("current")
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Inject
    private AuthorizationManager authorizationManager;

    @Inject
    @Named("current")
    private AttachmentReferenceResolver<String> attachmentReferenceResolver;

    @Inject
    private Provider<CollaboraConfiguration> configurationProvider;

    private Map<String, FileToken> tokens = new HashMap<>();

    /**
     * Get {@link FileToken} corresponding to the given user and file, or create a new token in case it does not exist
     * or is expired.
     *
     * @param userReference current user reference
     * @param fileId id of the edited file
     * @param requestedMode the requested action for the file
     * @return existing {@link FileToken}, or a new one
     */
    public FileToken getToken(DocumentReference userReference, String fileId, String requestedMode)
    {
        String user = userReference != null ? this.referenceSerializer.serialize(userReference) : XWIKI_GUEST;
        FileToken token = getExistingToken(user, fileId);
        if (token != null) {
            token.setActionMode(ActionMode.fromString(requestedMode));
            updateAccessRights(token);
            if (token.isExpired()) {
                tokens.remove(token.toString());
            } else {
                token.setUsage(token.getUsage() + 1);
                return token;
            }
        }

        return createNewToken(user, fileId, configurationProvider.get().getTokenTimeout(), requestedMode);
    }

    /**
     * Check if the given token has a valid form, exists, and it's not expired.
     *
     * @param token {@code String} representation of a token
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean isInvalid(String token)
    {
        FileToken foundToken = tokens.get(token);

        return foundToken == null || foundToken.isExpired();
    }

    /**
     * Check if the given token has a valid form, exists, and it's not expired.
     *
     * @param fileId id of the edited file
     * @param userReference {@link DocumentReference} user reference associated with the checked token
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean isInvalid(String fileId, DocumentReference userReference)
    {
        String user = userReference != null ? this.referenceSerializer.serialize(userReference) : XWIKI_GUEST;
        FileToken token = getExistingToken(user, fileId);

        return token == null || token.isExpired();
    }

    /**
     * Extends token time out with the Collabora configuration value.
     *
     * @param fileId id of the edited file
     * @param userReference {@link DocumentReference} user reference associated with the checked token
     */
    public void extendToken(String fileId, DocumentReference userReference)
    {
        String user = userReference != null ? this.referenceSerializer.serialize(userReference) : XWIKI_GUEST;
        FileToken fileToken = getExistingToken(user, fileId);
        if (fileToken != null) {
            fileToken.extendTokenTimeout(configurationProvider.get().getTokenTimeout());
        }
    }

    /**
     * Remove the usage of a token. This could mean to update the number of usages (i.e. number of windows where the
     * current file is edited by this user), or to simply remove the token in case it is no longer used.
     *
     * @param userReference current user reference
     * @param fileId id of the edited file
     * @param requestedMode the requested action for the file
     * @return the number of token usages
     */
    public int clearToken(DocumentReference userReference, String fileId, String requestedMode)
    {
        String user = userReference != null ? this.referenceSerializer.serialize(userReference) : XWIKI_GUEST;
        FileToken foundToken = getExistingToken(user, fileId);
        if (foundToken == null) {
            return 0;
        }
        foundToken.setActionMode(ActionMode.fromString(requestedMode));
        updateAccessRights(foundToken);
        int tokenUsage = foundToken.getUsage();
        if (tokenUsage > 1) {
            tokenUsage--;
            foundToken.setUsage(tokenUsage);
            logger.debug("Cleared token for file [{}] and user [{}]. Number of remained usages: [{}]", fileId, user,
                tokenUsage);
        } else {
            tokens.remove(foundToken.toString());
            tokenUsage = 0;
            logger.debug("Deleted token for file [{}] and user [{}].", fileId, user);
        }

        return tokenUsage;
    }

    /**
     * Extract the user reference from a token.
     *
     * @param token the token
     * @return the user reference, or null in case this token is invalid
     */
    public DocumentReference getTokenUserDocReference(String token)
    {
        FileToken fileToken = tokens.get(token);
        if (fileToken.getUser() == null) {
            return null;
        }
        return this.documentReferenceResolver.resolve(fileToken.getUser());
    }

    /**
     * @param token string representation of the token
     * @return {@code true} if this token has edit rights, {@code false} otherwise
     */
    public boolean hasWriteAccess(String token)
    {
        return tokens.get(token).hasEdit();
    }

    /**
     * @param token string representation of the token
     * @return {@code true} if this token has view or edit rights for accessing the file, {@code false} otherwise
     */
    public boolean hasAccess(String token)
    {
        FileToken fileToken = tokens.get(token);
        updateAccessRights(fileToken);
        return fileToken.hasView() || fileToken.hasEdit();
    }

    /**
     * @param fileId id of the edited file
     * @param userReference {@link DocumentReference} user reference associated with the checked token
     * @param requestedMode the requested action for the file
     * @return {@code true} if this token has view or edit rights for accessing the file, {@code false} otherwise
     */
    public boolean hasAccess(String fileId, DocumentReference userReference, String requestedMode)
    {
        String user = userReference != null ? this.referenceSerializer.serialize(userReference) : XWIKI_GUEST;
        FileToken fileToken = getExistingToken(user, fileId);
        if (fileToken != null) {
            fileToken.setActionMode(ActionMode.fromString(requestedMode));
            updateAccessRights(fileToken);
            return fileToken.hasView() || fileToken.hasEdit();
        }
        return false;
    }

    /**
     * Update token access rights in case they have been changed since the creation, e.g. you had edit right when you
     * first accessed the document with Collabora, but it has been denied since then, so you should not be able to edit
     * anymore.
     *
     * @param fileToken the token
     */
    private void updateAccessRights(FileToken fileToken)
    {
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileToken.getFileId());

        boolean hasView =
            this.authorizationManager.hasAccess(Right.VIEW, this.documentReferenceResolver.resolve(fileToken.getUser()),
                attachmentReference.getDocumentReference());
        if (hasView != fileToken.hasView()) {
            fileToken.setHasView(hasView);
            logger.debug("View right changed for existing token of file [{}]", fileToken.getFileId());
        }
        boolean isEdit = false;
        if (fileToken.isEditRequested()) {
            isEdit = this.authorizationManager.hasAccess(Right.EDIT,
                this.documentReferenceResolver.resolve(fileToken.getUser()),
                attachmentReference.getDocumentReference());
        }
        if (isEdit != fileToken.hasEdit()) {
            fileToken.setHasEdit(isEdit);
            logger.debug("Edit right changed for existing token of file [{}]", fileToken.getFileId());
        }
    }

    private FileToken getExistingToken(String user, String fileId)
    {
        Optional<Map.Entry<String, FileToken>> tokenEntry = this.tokens.entrySet().stream()
            .filter(x -> x.getValue().getUser().equals(user) && x.getValue().getFileId().equals(fileId)).findFirst();

        return tokenEntry.map(Map.Entry::getValue).orElse(null);
    }

    private FileToken createNewToken(String user, String fileId, int tokenTimeout, String requestedMode)
    {
        AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(fileId);
        boolean isEdit = false;
        if (!requestedMode.equals(VIEW_MODE_KEY)) {
            isEdit = this.authorizationManager.hasAccess(Right.EDIT, this.documentReferenceResolver.resolve(user),
                attachmentReference.getDocumentReference());
        }
        FileToken token = new FileToken(user, fileId, tokenTimeout,
            this.authorizationManager.hasAccess(Right.VIEW, this.documentReferenceResolver.resolve(user),
                attachmentReference.getDocumentReference()), isEdit);
        token.setActionMode(ActionMode.fromString(requestedMode));
        tokens.put(token.toString(), token);
        logger.debug("New token created for file [{}] and user [{}],", fileId, user);

        return token;
    }
}

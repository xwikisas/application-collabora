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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;

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
    @Inject
    private Logger logger;

    @Inject
    private EntityReferenceSerializer<String> referenceSerializer;

    @Inject
    @Named("current")
    private DocumentReferenceResolver<String> documentReferenceResolver;

    private Map<String, FileToken> tokens = new HashMap<>();

    /**
     * Get {@link FileToken} corresponding to the given user and file, or create a new token in case it does not exist
     * or is expired.
     *
     * @param userReference current user reference
     * @param fileId id of the edited file
     * @return existing {@link FileToken}, or a new one
     */
    public FileToken getToken(DocumentReference userReference, String fileId)
    {
        String user = this.referenceSerializer.serialize(userReference);
        String key = getKey(user, fileId);
        FileToken token = tokens.get(key);
        if (token != null) {
            if (token.isExpired()) {
                tokens.remove(key);
            } else {
                token.setUsage(token.getUsage() + 1);
                return token;
            }
        }

        return createNewToken(user, fileId);
    }

    /**
     * Check if the given token has a valid form, exists, and it's not expired.
     *
     * @param token {@code String} representation of a token
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean isInvalid(String token)
    {
        FileToken givenToken = new FileToken(token);
        FileToken foundToken = tokens.get(getKey(givenToken.getUser(), givenToken.getFileId()));
        return foundToken == null || !foundToken.equals(givenToken) || foundToken.isExpired();
    }

    /**
     * Remove the usage of a token. This could mean to update the number of usages (i.e. number of windows where the
     * current file is edited by this user), or to simply remove the token in case it is no longer used.
     *
     * @param userReference current user reference
     * @param fileId id of the edited file
     * @return the number of token usages
     */
    public int clearToken(DocumentReference userReference, String fileId)
    {
        String key = getKey(this.referenceSerializer.serialize(userReference), fileId);
        FileToken foundToken = tokens.get(key);
        if (foundToken == null) {
            return 0;
        }

        int tokenUsage = foundToken.getUsage();
        if (tokenUsage > 1) {
            tokenUsage--;
            foundToken.setUsage(tokenUsage);
            logger.debug("Cleared token for file [{}] and user [{}]. Number of remained usages: [{}]", fileId,
                this.referenceSerializer.serialize(userReference), tokenUsage);
        } else {
            tokens.remove(key);
            tokenUsage = 0;
            logger.debug("Deleted token for file [{}] and user [{}].", fileId,
                this.referenceSerializer.serialize(userReference));
        }

        return tokenUsage;
    }

    /**
     * Extract the user reference from a token.
     *
     * @param token the token
     * @return the user reference, or null in case this token is invalid
     */
    public DocumentReference getTokenUserReference(String token)
    {
        FileToken fileToken = new FileToken(token);
        if (fileToken.getUser() == null) {
            return null;
        }
        return this.documentReferenceResolver.resolve(fileToken.getUser());
    }

    private FileToken createNewToken(String user, String fileId)
    {
        FileToken token = new FileToken(user, fileId);
        tokens.put(getKey(user, fileId), token);
        logger.debug("New token created for file [{}] and user [{}],", fileId, user);

        return token;
    }

    private String getKey(String user, String fileId)
    {
        return String.format("%s_%s", user, fileId);
    }
}

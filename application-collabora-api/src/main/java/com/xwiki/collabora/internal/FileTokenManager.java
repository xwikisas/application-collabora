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

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;

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
    private Map<String, FileToken> tokens = new HashMap<>();

    /**
     * Get {@link FileToken} corresponding to the given user and file, or create a new token in case it does not exist
     * or is expired.
     *
     * @param user {@code String} representation for the current user reference
     * @param fileId id of the edited file
     * @return existing {@link FileToken}, or a new one
     */
    public FileToken getToken(String user, String fileId)
    {
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
     * @param user {@code String} representation for the current user
     * @param fileId id of the edited file
     */
    public void clearToken(String user, String fileId)
    {
        String key = getKey(user, fileId);
        FileToken foundToken = tokens.get(key);
        if (foundToken == null) {
            return;
        }

        int tokenUsage = foundToken.getUsage();
        if (tokenUsage > 1) {
            foundToken.setUsage(tokenUsage - 1);
        } else {
            tokens.remove(key);
        }
    }

    private FileToken createNewToken(String user, String fileId)
    {
        FileToken token = new FileToken(user, fileId);
        tokens.put(getKey(user, fileId), token);

        return token;
    }

    private String getKey(String user, String fileId)
    {
        return String.format("%s_%s", user, fileId);
    }
}

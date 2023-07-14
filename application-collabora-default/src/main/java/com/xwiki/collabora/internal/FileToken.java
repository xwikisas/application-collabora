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

import java.security.SecureRandom;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Token used in order to authenticate requests done using the WOPI protocol.
 *
 * @version $Id$
 * @since 1.0
 */
public class FileToken
{
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final String user;

    private final String fileId;

    private final Long timestamp;

    private final int randomNumber;

    /**
     * Token timeout, in seconds.
     */
    private final int tokenTimeout;

    private boolean hasView;

    private boolean hasEdit;

    private int usage;

    FileToken(String user, String fileId, int tokenTimeout, boolean hasView, boolean hasEdit)
    {
        this.user = user;
        this.fileId = fileId;
        this.timestamp = new Date().getTime();
        this.randomNumber = Math.abs(SECURE_RANDOM.nextInt());
        this.usage = 1;
        this.hasView = hasView;
        this.hasEdit = hasEdit;
        // Transform from hours to seconds.
        this.tokenTimeout = tokenTimeout * 1200;
    }

    /**
     * Check if this token is expired. Tokens have a default lifetime of 5 hours, but this value can be configured.
     *
     * @return {@code true} if the token has expired, {@code false} otherwise.
     */
    public boolean isExpired()
    {
        long currentTime = new Date().getTime();
        long differenceInSec = (currentTime - timestamp) / 1000;
        return differenceInSec > this.tokenTimeout;
    }

    /**
     * Get the number of places where this token is used (i.e. number of windows where the current file is edited by
     * this user).
     *
     * @return the number of places where this token is used
     */
    public int getUsage()
    {
        return this.usage;
    }

    /**
     * @param usage the number of places where this token is used
     */
    public void setUsage(int usage)
    {
        this.usage = usage;
    }

    /**
     * @return the user corresponding to this token
     */
    public String getUser()
    {
        return this.user;
    }

    /**
     * @return the id of the file corresponding to this token
     */
    public String getFileId()
    {
        return this.fileId;
    }

    /**
     * @return token creation time
     */
    public Long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return {@code true} if this token has view rights, {@code false} otherwise
     */
    public boolean hasView()
    {
        return this.hasView;
    }

    /**
     * @param hasView {@code true} if this token has view rights, {@code false} otherwise
     */
    public void setHasView(boolean hasView)
    {
        this.hasView = hasView;
    }

    /**
     * @return {@code true} if this token has edit rights, {@code false} otherwise
     */
    public boolean hasEdit()
    {
        return this.hasEdit;
    }

    /**
     * @param hasEdit {@code true} if this token has edit rights, {@code false} otherwise
     */
    public void setHasEdit(boolean hasEdit)
    {
        this.hasEdit = hasEdit;
    }

    @Override
    public String toString()
    {
        return String.format("wopi_%s_%s_%s_%s", this.user, this.fileId, this.timestamp, this.randomNumber);
    }

    @Override
    public int hashCode()
    {

        return new HashCodeBuilder().append(this.getUser()).append(this.getFileId()).append(this.getTimestamp())
            .append(this.getRandomNumber()).toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof FileToken)) {
            return false;
        }
        FileToken other = (FileToken) obj;
        EqualsBuilder builder = new EqualsBuilder();

        builder.append(this.getUser(), other.getUser());
        builder.append(this.getFileId(), other.getFileId());
        builder.append(this.getTimestamp(), other.getTimestamp());
        builder.append(this.getRandomNumber(), other.getRandomNumber());

        return builder.build();
    }

    private int getRandomNumber()
    {
        return this.randomNumber;
    }
}

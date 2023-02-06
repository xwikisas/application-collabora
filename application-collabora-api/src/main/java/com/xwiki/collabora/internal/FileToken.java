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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class FileToken
{
    private String userReference;

    private String fileId;

    private Long timestamp;

    private int randomNumber;

    private int usage;

    FileToken(String userReference, String fileId)
    {
        this.userReference = userReference;
        this.fileId = fileId;
        this.timestamp = new Date().getTime();
        this.randomNumber = Math.abs(new SecureRandom().nextInt());
        this.usage = 1;
    }

    FileToken(String token)
    {
        String pattern = "wopi_(.+)_(.+)_(\\d+)_(\\d+)";
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(token);
        if (m.find()) {
            this.userReference = m.group(1);
            this.fileId = m.group(2);
            this.timestamp = Long.parseLong(m.group(3));
            this.randomNumber = Integer.parseInt(m.group(4));
            this.usage = 1;
        }
    }

    /**
     * Tokens have a valability of 30 minutes.
     *
     * @return true if the token has expired, or false otherwise.
     */
    public boolean isExpired()
    {
        long currentTime = new Date().getTime();
        long differenceInSec = (currentTime - timestamp) / 1000;
        return differenceInSec > 1800;
    }

    public int getUsage()
    {
        return this.usage;
    }

    public void setUsage(int usage)
    {
        this.usage = usage;
    }

    public String getUserReference()
    {
        return this.userReference;
    }

    public String getFileId()
    {
        return this.fileId;
    }

    public Long getTimestamp()
    {
        return this.timestamp;
    }

    private long getRandomNumber()
    {
        return this.randomNumber;
    }

    @Override
    public String toString()
    {
        return String.format("wopi_%s_%s_%s_%s", this.userReference, this.fileId, this.timestamp, this.randomNumber);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof FileToken)) {
            return false;
        }
        FileToken other = (FileToken) obj;
        EqualsBuilder builder = new EqualsBuilder();

        builder.append(this.getUserReference(), other.getUserReference());
        builder.append(this.getFileId(), other.getFileId());
        builder.append(this.getTimestamp(), other.getTimestamp());
        builder.append(this.getRandomNumber(), other.getRandomNumber());

        return builder.build();
    }
}

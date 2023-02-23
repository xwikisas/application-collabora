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
package com.xwiki.collabora.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.rest.XWikiRestComponent;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.stability.Unstable;

import com.xwiki.collabora.rest.model.jaxb.Token;

/**
 * Provides the APIs needed by the collabora server in order to access a file and it's content, but also to save it.
 *
 * @version $Id$
 * @since 1.0
 */
@Path("/collabora/files/{id}")
@Unstable
public interface Wopi extends XWikiRestComponent
{
    /**
     * Get file info.
     *
     * @param fileId id of the file
     * @param token {@code String} representation of the authentication token
     * @param userCanWrite {@code true} if the user has write access, {@code false} otherwise
     * @return information about the requested file
     * @throws XWikiRestException if an error occurred while accessing the file
     */
    @GET
    Response get(@PathParam("id") String fileId, @QueryParam("access_token") String token,
        @QueryParam("userCanWrite") String userCanWrite) throws XWikiRestException;

    /**
     * Get file content.
     *
     * @param fileId id of the file
     * @param token {@code String} representation of the authentication token
     * @return the content of this file
     * @throws XWikiRestException if an error occurred while accessing the file
     */
    @GET
    @Path("/contents")
    Response getContents(@PathParam("id") String fileId, @QueryParam("access_token") String token)
        throws XWikiRestException;

    /**
     * Update content of a file. This is used by the Collabora Save action.
     *
     * @param fileId id of the file
     * @param token {@code String} representation of the authentication token
     * @param body file content
     * @return information about the updated file
     * @throws XWikiRestException if an error occurred while updating the file
     */
    @POST
    @Path("/contents")
    Response postContents(@PathParam("id") String fileId, @QueryParam("access_token") String token, byte[] body)
        throws XWikiRestException;

    /**
     * Get information specific to this type of file, to know which part of Collabora online to load. Get information
     * specific to this file, to be able to load it (e.g. token needed for authenticating requests, file type Collabora
     * particularities).
     *
     * @param fileId id of the file
     * @return information needed by Collabora to load
     * @throws XWikiRestException if an error occurred while getting information
     */
    @GET
    @Path("/token")
    Token getToken(@PathParam("id") String fileId) throws XWikiRestException;

    /**
     * Clear saved token for this file. Consider that this user might be editing the file in another window, so don't
     * remove the token entirely in this case.
     *
     * @param fileId id of the file
     * @return information about the usage of this file token
     * @throws XWikiRestException if an error occurred while removing the token
     */
    @POST
    @Path("/token")
    Token clearToken(@PathParam("id") String fileId) throws XWikiRestException;
}

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
import javax.ws.rs.PUT;
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
@Path("/collabora/files/{id}/{mode}")
@Unstable
public interface Wopi extends XWikiRestComponent
{
    /**
     * Get file info. A specific REST model should be used as response, but this results in some problems. The request
     * to this resource is done by the Collabora server, and it expects a JSON response with Pascal Case fields names,
     * which cannot be achieved when using a jaxb model to generate the POJO.
     *
     * @param fileId id of the file
     * @param mode the requested action for the file
     * @param token {@code String} representation of the authentication token
     * @return information about the requested file
     * @throws XWikiRestException if an error occurred while accessing the file
     */
    @GET
    Response get(@PathParam("id") String fileId, @PathParam("mode") String mode,
        @QueryParam("access_token") String token) throws XWikiRestException;

    /**
     * Get file content.
     *
     * @param fileId id of the file
     * @param mode the requested action for the file
     * @param token {@code String} representation of the authentication token
     * @return the content of this file
     * @throws XWikiRestException if an error occurred while accessing the file
     */
    @GET
    @Path("/contents")
    Response getContents(@PathParam("id") String fileId, @PathParam("mode") String mode,
        @QueryParam("access_token") String token)
        throws XWikiRestException;

    /**
     * Update content of a file. This is used by the Collabora Save action. A specific REST model should be used as
     * response, see {@link #get(String, String)}. This should be a PUT method, but the Collabora online server uses a
     * POST verb on the save action.
     *
     * @param fileId id of the file
     * @param mode the requested action for the file
     * @param token {@code String} representation of the authentication token
     * @param body file content
     * @return information about the updated file
     * @throws XWikiRestException if an error occurred while updating the file
     */
    @POST
    @Path("/contents")
    Response postContents(@PathParam("id") String fileId, @PathParam("mode") String mode,
        @QueryParam("access_token") String token, byte[] body)
        throws XWikiRestException;

    /**
     * Get information specific to this type of file, to know which part of Collabora online to load. Get information
     * specific to this file, to be able to load it (e.g. token needed for authenticating requests, file type Collabora
     * particularities).
     *
     * @param fileId id of the file
     * @param mode the requested action for the file
     * @return information needed by Collabora to load
     * @throws XWikiRestException if an error occurred while getting information
     */
    @GET
    @Path("/token")
    Token getToken(@PathParam("id") String fileId, @PathParam("mode") String mode) throws XWikiRestException;

    /**
     * Clear saved token for this file. Consider that this user might be editing the file in another window, so don't
     * remove the token entirely in this case. This should be a PUT method, but it is used inside a sendBeacon call, and
     * right now it only supports POST, see https://developer.mozilla.org/en-US/docs/Web/API/Navigator/sendBeacon
     *
     * @param fileId id of the file
     * @param mode the requested action for the file
     * @return information about the usage of this file token
     * @throws XWikiRestException if an error occurred while removing the token
     */
    @POST
    @Path("/token")
    Token clearToken(@PathParam("id") String fileId, @PathParam("mode") String mode) throws XWikiRestException;

    /**
     * If the token for the given file is associated to the current user and has expired, extends the expiration time of
     * the token. The token's timeout is extended based on the Collabora configuration settings.
     *
     * @param fileId id of the file
     * @param mode the requested action for the file
     * @return status code of the operation
     * @throws XWikiRestException if an error occurred while extending the token
     */
    @PUT
    @Path("/token/extend")
    Response updateTokenExpiration(@PathParam("id") String fileId, @PathParam("mode") String mode)
        throws XWikiRestException;
}

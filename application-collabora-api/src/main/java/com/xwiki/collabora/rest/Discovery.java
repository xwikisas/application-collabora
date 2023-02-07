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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.rest.XWikiRestException;
import org.xwiki.stability.Unstable;

/**
 * To set up the iframe, the WOPI host (the application) needs to read a discovery XML from a defined location on the
 * WOPI client (the Collabora Online server). The discovery is available at:
 * https://<WOPIClientURL>:<port>/hosting/discovery. The reply is discovery.xml that contains urlsrc for various file
 * formats. The urlsrc needs to be used in the iframe for editing the document. Create also a token  which will be used
 * to authenticate the following requests on this edited file.
 *
 * @version $Id$
 * @since 1.0
 */
@Path("/discovery")
@Unstable
public interface Discovery
{
    /**
     * Get information specific to this type of file, to know which part of Collabora online to load. Get information
     * specific to this file, to be able to load it (e.g. token needed for authenticating requests, file type Collabora
     * particularities).
     *
     * @param server Collabora server
     * @param fileId id of the file
     * @return information needed by Collabora to load
     * @throws XWikiRestException if an error occurred while getting information
     */
    @GET
    Response getDiscovery(@QueryParam("server") String server, @QueryParam("fileId") String fileId)
        throws XWikiRestException;

    /**
     * Clear saved token for this file. Consider that this user might be editing the file in another window, so don't
     * remove the token entirely in this case.
     *
     * @param fileId id of the file
     * @return information about the usage of this file token
     * @throws XWikiRestException if an error occurred while removing the token
     */
    @POST
    @Path("/clearToken")
    Response clearToken(@QueryParam("fileId") String fileId) throws XWikiRestException;
}

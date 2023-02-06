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
 * https://<WOPIClientURL>:<port>/hosting/discovery The reply is discovery.xml that contains urlsrc for various file
 * formats. The urlsrc needs to be used in the iframe for editing the document.
 *
 * @since 1.0
 */
@Path("/discovery")
@Unstable
public interface Discovery
{
    @GET
    Response getDiscovery(@QueryParam("server") String server, @QueryParam("ext") String ext,
        @QueryParam("fileId") String fileId) throws XWikiRestException;

    @POST
    @Path("/clearToken")
    Response clearToken(@QueryParam("fileId") String fileId) throws XWikiRestException;
}

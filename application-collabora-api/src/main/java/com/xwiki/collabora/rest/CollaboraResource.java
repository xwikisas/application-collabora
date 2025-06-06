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
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.rest.XWikiRestComponent;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.stability.Unstable;

/**
 * Provides the APIs needed by the collabora application.
 *
 * @version $Id$
 * @since 1.5.3
 */
@Path("/collabora")
@Unstable
public interface CollaboraResource extends XWikiRestComponent
{
    /**
     * Check the view and edit rights the user has on a given document.
     *
     * @param documentRef {@code String} representation of the document reference
     * @return canEdit response with value {@code true} if the user has edit rights, or {@code false} otherwise
     * @throws XWikiRestException with code 500 if an error occurs, or 401 if the user has no view rights on the
     *     document
     */
    @GET
    @Path("/rights")
    Response checkRights(@QueryParam("document_ref") String documentRef) throws XWikiRestException;
}

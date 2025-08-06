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
package com.xwiki.collabora.internal.rest;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.security.authorization.AccessDeniedException;
import org.xwiki.security.authorization.ContextualAuthorizationManager;
import org.xwiki.security.authorization.Right;

import com.xpn.xwiki.XWikiContext;
import com.xwiki.collabora.rest.CollaboraResource;

/**
 * Default implementation of {@link CollaboraResource}.
 *
 * @version $Id$
 * @since 1.5.3
 */
@Component
@Named("com.xwiki.collabora.internal.rest.DefaultCollaboraResource")
@Singleton
public class DefaultCollaboraResource implements CollaboraResource
{
    @Inject
    private Provider<XWikiContext> xcontextProvider;

    @Inject
    private ContextualAuthorizationManager contextualAuthorizationManager;

    @Inject
    @Named("current")
    private DocumentReferenceResolver<String> referenceResolver;

    @Inject
    private Logger logger;

    @Override
    public Response checkRights(String documentRef) throws XWikiRestException
    {
        try {
            DocumentReference docRef = referenceResolver.resolve(documentRef);
            this.contextualAuthorizationManager.checkAccess(Right.VIEW, docRef);
            boolean canEdit = this.contextualAuthorizationManager.hasAccess(Right.EDIT, docRef);
            return Response.ok(Map.of("canEdit", canEdit)).type(MediaType.APPLICATION_JSON).build();
        } catch (AccessDeniedException deniedException) {
            logger.warn("User has no view rights on the document: [{}].", documentRef);
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } catch (Exception e) {
            logger.warn("Failed to check user rights on document [{}]. Root cause: [{}]", documentRef,
                ExceptionUtils.getRootCauseMessage(e));
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}

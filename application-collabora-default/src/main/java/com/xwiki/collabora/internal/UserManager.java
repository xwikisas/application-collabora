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

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.user.UserProperties;
import org.xwiki.user.UserPropertiesResolver;
import org.xwiki.user.UserReferenceResolver;

/**
 * Access properties of a user.
 *
 * @version $Id$
 * @since 1.2.2
 */
@Component(roles = UserManager.class)
@Singleton
public class UserManager
{
    @Inject
    @Named("document")
    private UserReferenceResolver<DocumentReference> userReferenceResolver;

    @Inject
    private UserPropertiesResolver userPropertiesResolver;

    /**
     * @param userDocReference user document reference
     * @return the name of the user in firstName lastName format
     */
    public String getUserFriendlyName(DocumentReference userDocReference)
    {
        UserProperties userProperties = userPropertiesResolver.resolve(userReferenceResolver.resolve(userDocReference));
        String firstName = Objects.toString(userProperties.getFirstName(), "");
        String lastName = Objects.toString(userProperties.getLastName(), "");
        String userFriendlyName = String.format("%s %s", firstName, lastName).trim();

        // Display the page name as a fallback for users without first and last name.
        return StringUtils.isEmpty(userFriendlyName) ? userDocReference.getName() : userFriendlyName;
    }
}

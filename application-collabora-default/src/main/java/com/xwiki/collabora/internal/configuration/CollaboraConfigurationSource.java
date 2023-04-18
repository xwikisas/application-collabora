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
package com.xwiki.collabora.internal.configuration;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

/**
 * Collabora configuration source corresponding to the current wiki.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Named(CollaboraConfigurationSource.HINT)
@Singleton
public class CollaboraConfigurationSource extends AbstractCollaboraConfigurationSource
{
    /**
     * The hint for this component.
     */
    public static final String HINT = "collabora.configuration.current";

    @Override
    protected DocumentReference getDocumentReference()
    {
        return new DocumentReference(AbstractCollaboraConfigurationSource.CONFIG_DOC, this.getCurrentWikiReference());
    }

    @Override
    protected String getCacheId()
    {
        return HINT;
    }
}

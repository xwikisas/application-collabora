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

import javax.inject.Inject;
import javax.inject.Named;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;

import groovy.lang.Singleton;

/**
 * Collabora configuration source corresponding to the main wiki.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named(MainCollaboraConfigurationSource.HINT)
@Singleton
public class MainCollaboraConfigurationSource extends AbstractCollaboraConfigurationSource
{
    /**
     * The hint for this component.
     */
    public static final String HINT = "collabora.configuration.main";

    @Inject
    protected WikiDescriptorManager wikiManager;

    @Override
    protected DocumentReference getDocumentReference()
    {
        return new DocumentReference(AbstractCollaboraConfigurationSource.CONFIG_DOC,
            new WikiReference(this.wikiManager.getMainWikiId()));
    }

    @Override
    protected String getCacheId()
    {
        return HINT;
    }
}

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
package com.xwiki.collabora.script;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import com.xwiki.collabora.configuration.CollaboraConfiguration;

/**
 * Collabora script services.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named("collabora")
@Singleton
@Unstable
public class CollaboraScriptService implements ScriptService
{
    @Inject
    private CollaboraConfiguration configuration;

    /**
     * @return the Collabora configuration
     * @since 1.1
     */
    @Unstable
    public CollaboraConfiguration getConfiguration()
    {
        return configuration;
    }
}

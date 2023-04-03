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
 * Script services to access the Collabora configurations.
 *
 * @version $Id$
 * @since 1.1
 */
@Component
@Named("collaboraConfiguration")
@Singleton
@Unstable
public class CollaboraConfigurationScriptService implements ScriptService
{
    @Inject
    private CollaboraConfiguration configuration;

    /**
     * Check if Collabora is enabled. Fallback on the main wiki configuration in case it was not defined at the wiki
     * level.
     *
     * @return {@code true} if Collabora is enabled, {@code false} otherwise
     * @since 1.1
     */
    @Unstable
    public boolean isEnabled()
    {
        Boolean isEnabled = this.configuration.isEnabled();
        return isEnabled != null ? isEnabled : false;
    }
}

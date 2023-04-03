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
package com.xwiki.collabora.configuration;

import java.net.MalformedURLException;
import java.net.URL;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

/**
 * Collabora configurations.
 *
 * @version $Id$
 * @since 1.0
 */
@Role
@Unstable
public interface CollaboraConfiguration
{
    /**
     * Get the discovery URL, needed for accessing the urlSrc specific to each file format. This is available at
     * https://WOPIClientURL:port/hosting/discovery.
     *
     * @return the discovery {@link URL}
     * @throws MalformedURLException In case an error occurred while creating the {@link URL}
     */
    URL getDiscoveryURL() throws MalformedURLException;

    /**
     * Check if Collabora is enabled. Fallback on the main wiki configuration in case it was not defined at the wiki
     * level.
     *
     * @return {@code true} if Collabora is enabled, {@code false} when is disabled or not specified
     * @since 1.1
     */
    boolean isEnabled();
}

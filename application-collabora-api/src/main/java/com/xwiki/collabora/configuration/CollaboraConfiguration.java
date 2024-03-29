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

    /**
     * Get the URL of the Collabora server. Fallback on the main wiki server configuration in case it was not defined at
     * the wiki level.
     *
     * @return the Collabora Online server URL
     * @since 1.1
     */
    String getServerURL();

    /**
     * For editing files with Collabora, the domain from where this is done must be declared in the Collabora Online
     * server configurations. To avoid adding each subwiki domain in the configuration, the main wiki can be used as an
     * editing endpoint.
     *
     * @return {@code true} if the main wiki will be used for editing files, {@code false} if the current wiki will be
     *     used
     */
    boolean editUsingMainWiki();

    /**
     * Get the number of hours (e.g. 1 for one hour, 2 for 2 hours) after which the file token will expire and edit will
     * stop working. If not defined on the sub wiki or main wiki, a default value of 5 is used.
     *
     * @return the number of hours after which the file editing token expires.
     */
    int getTokenTimeout();
}

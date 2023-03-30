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

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;

import com.xwiki.collabora.configuration.CollaboraConfiguration;

import liquibase.util.StringUtils;

/**
 * Default implementation of {@link CollaboraConfiguration}.
 *
 * @version $Id$
 * @since 1.0
 */
@Component
@Singleton
public class DefaultCollaboraConfiguration implements CollaboraConfiguration
{
    private static final String SERVER = "server";

    @Inject
    @Named(MainCollaboraConfigurationSource.HINT)
    private ConfigurationSource mainConfiguration;

    @Inject
    @Named(CollaboraConfigurationSource.HINT)
    private ConfigurationSource currentConfiguration;

    @Override
    public URL getDiscoveryURL() throws MalformedURLException
    {

        return new URL(getServerConfiguration() + "/hosting/discovery");
    }

    /**
     * Fallback on the main wiki server configuration in case it was not defined at the wiki level.
     *
     * @return the Collabora Online server defined in configuration
     */
    private String getServerConfiguration()
    {
        String currentWikiServer = this.currentConfiguration.getProperty(SERVER);
        String mainWikiServer = this.mainConfiguration.getProperty(SERVER);
        return StringUtils.isEmpty(currentWikiServer) ? mainWikiServer : currentWikiServer;
    }
}

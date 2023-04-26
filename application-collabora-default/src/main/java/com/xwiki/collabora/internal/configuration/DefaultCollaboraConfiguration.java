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

import org.apache.commons.lang3.StringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;

import com.xwiki.collabora.configuration.CollaboraConfiguration;

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
    private static final String EDIT_USING_MAIN_WIKI = "editUsingMainWiki";

    private static final String IS_ENABLED = "isEnabled";

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
        return new URL(this.getServerURL() + "/hosting/discovery");
    }

    @Override
    public boolean isEnabled()
    {
        Boolean isCurrentWikiEnabled = this.currentConfiguration.getProperty(IS_ENABLED, Boolean.class);
        return isCurrentWikiEnabled == null ? this.mainConfiguration.getProperty(IS_ENABLED, false)
            : isCurrentWikiEnabled;
    }

    @Override
    public String getServerURL()
    {
        String currentWikiServer = this.currentConfiguration.getProperty(SERVER);
        return StringUtils.isEmpty(currentWikiServer) ? this.mainConfiguration.getProperty(SERVER) : currentWikiServer;
    }

    @Override
    public boolean editUsingMainWiki()
    {
        Boolean editUsingMainWiki = this.currentConfiguration.getProperty(EDIT_USING_MAIN_WIKI, Boolean.class);
        return editUsingMainWiki == null ? this.mainConfiguration.getProperty(EDIT_USING_MAIN_WIKI, false)
            : editUsingMainWiki;
    }
}

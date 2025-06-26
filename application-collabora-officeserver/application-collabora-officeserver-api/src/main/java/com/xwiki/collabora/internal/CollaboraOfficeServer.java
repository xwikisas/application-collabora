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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.officeimporter.converter.OfficeConverter;
import org.xwiki.officeimporter.server.OfficeServer;
import org.xwiki.officeimporter.server.OfficeServerException;

import com.xwiki.collabora.configuration.CollaboraConfiguration;

/**
 * Office server implementation based on a connection to Collabora Server.
 *
 * @version $Id$
 * @since 1.6
 */
@Component
@Singleton
public class CollaboraOfficeServer implements OfficeServer
{
    @Inject
    private CollaboraConfiguration collaboraConfiguration;

    @Override
    public ServerState getState()
    {
        return collaboraConfiguration.isEnabled() ? ServerState.CONNECTED : ServerState.NOT_CONNECTED;
    }

    @Override
    public void start() throws OfficeServerException
    {
        // Do nothing as we don't control the server lifecycle
    }

    @Override
    public void stop() throws OfficeServerException
    {
        // Do nothing as we don't control the server lifecycle
    }

    @Override
    public OfficeConverter getConverter()
    {
        return new CollaboraOfficeConverter(collaboraConfiguration);
    }
}

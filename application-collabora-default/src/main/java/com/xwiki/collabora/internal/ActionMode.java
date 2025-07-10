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

/**
 * Represents the request options for a document opened with Collabora.
 *
 * @version $Id$
 * @since 1.6.2
 */
public enum ActionMode
{
    /**
     * Edit request.
     */
    EDIT,
    /**
     * View request.
     */
    VIEW;

    /**
     * Associate the given {@link String} with an action mode. In case of error, or the given mode is not found, return
     * the EDIT mode by default.
     *
     * @param mode the requested action mode
     * @return the associated action mode, or EDIT mode by default
     */
    public static ActionMode fromString(String mode)
    {
        if (mode == null) {
            return ActionMode.EDIT;
        }
        try {
            return ActionMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ActionMode.EDIT;
        }
    }
}

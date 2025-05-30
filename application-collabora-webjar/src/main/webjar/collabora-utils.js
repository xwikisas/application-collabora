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
define('collabora-attachment', {
 prefix: 'collabora.attachment.',
 keys: [
   'edit.title',
   'view.title',
   'modal.submit.error'
 ]
});

define(['jquery', 'xwiki-l10n!collabora-attachment'], function($, l10n) {
  const collaboraUtils =  {
    getExtAcceptedAction: function() {
      const canDoByExt = {};
      ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'csv', 'rtf', 'txt', 'odt', 'ods', 'odp', 'odg']
        .forEach(function(x) {
          canDoByExt[x] = 'edit';
      });
      ['pdf', 'fb2'].forEach(function(x) {
        canDoByExt[x] = 'view';
      });
      return canDoByExt;
    },

    getAccessRights: function(fileName) {
      const fileType = fileName.slice(fileName.lastIndexOf('.') + 1).toLowerCase();
      const canDoByExt = collaboraUtils.getExtAcceptedAction();
      let accessRights = canDoByExt[fileType];
      if (!accessRights) {
        return false;
      }
      if (!XWiki.hasEdit) {
        accessRights = 'view';
      }
      return accessRights;
    },

    getAccessRightsForDocument: async function(fileName, documentRef) {
      const fileType = fileName.slice(fileName.lastIndexOf('.') + 1).toLowerCase();
      const canDoByExt = collaboraUtils.getExtAcceptedAction();
      var accessRights = canDoByExt[fileType];
      if (!accessRights) {
        return false;
      }
      if (typeof documentRef == 'undefined') {
        if (!XWiki.hasEdit) {
          accessRights = 'view';
        }
      } else {
        const protocol = window.location.protocol;
        const host = window.location.host;
        const contextPath = window.XWiki.contextPath || '/xwiki';
        const encodedDocRef = encodeURIComponent(documentRef);
        const restURL = `${protocol}//${host}${contextPath}/rest/collabora/rights?document_ref=${encodedDocRef}`;
        const response = await fetch(restURL, {
          headers: {
            'Accept': 'application/json'
          }
        });
        if (!response.ok) {
          return false;
        }
        const result = await response.json();
        if (!result.canEdit) {
           accessRights = 'view';
        }
      }
      return accessRights;
    },

    getActionURL: function(fileName, accessRights, editURL, document) {
      const queryString = $.param({
        'document': document ?? XWiki.Document.currentDocumentReference.toString(),
        'filename': fileName,
        'action': accessRights,
        'xpage': 'plain'
      });
      editURL += (editURL.indexOf('?') < 0 ? '?' : '&') + queryString;

      return editURL;
    },

    populateCollaboraButton: function(thisButton, fileName, accessRights, document) {
      const title = l10n.get(accessRights == 'edit' ? 'edit.title' : 'view.title');
      thisButton.attr('title', title);
      thisButton.find('img').attr('alt', title);

      thisButton.attr('href', collaboraUtils.getActionURL(fileName, accessRights, thisButton.data('editUrl'),
        document));
    }
  };
  return collaboraUtils;
});
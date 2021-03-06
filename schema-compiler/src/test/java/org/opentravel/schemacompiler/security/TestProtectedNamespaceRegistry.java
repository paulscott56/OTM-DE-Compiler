/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opentravel.schemacompiler.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.opentravel.schemacompiler.security.ProtectedNamespaceCredentials;
import org.opentravel.schemacompiler.security.ProtectedNamespaceGroup;
import org.opentravel.schemacompiler.security.ProtectedNamespaceRegistry;
import org.opentravel.schemacompiler.security.impl.DefaultProtectedNamespaceGroup;
import org.opentravel.schemacompiler.security.impl.DefaultProtectedNamespaceRegistry;
import org.opentravel.schemacompiler.util.URLUtils;

/**
 * Verifies the functions of the <code>ProtectedNamespaceRegistry</code> components.
 * 
 * @author S. Livezey
 */
public class TestProtectedNamespaceRegistry {

    @Test
    public void testProtectedNamespaceRecognition() throws Exception {
        ProtectedNamespaceRegistry nsRegistry = newProtectedNamespaceRegistry();

        assertTrue(nsRegistry.isProtectedNamespace("http://opentravel.org/common"));
        assertTrue(nsRegistry.isProtectedNamespace("http://opentravel.org/common/"));
        assertTrue(nsRegistry.isProtectedNamespace("http://opentravel.org/test"));
        assertTrue(nsRegistry.isProtectedNamespace("http://opentravel.org/test/"));
        assertTrue(nsRegistry.isProtectedNamespace("http://opentravel.org/test/project/v01"));
        assertFalse(nsRegistry.isProtectedNamespace("http://opentravel.org/etc/"));
        assertFalse(nsRegistry.isProtectedNamespace("http://www.sabre-holdings.com/STL/v02"));
    }

    @Test
    public void testProtectedNamespaceAccess() throws Exception {
        ProtectedNamespaceRegistry nsRegistry = newProtectedNamespaceRegistry();
        ProtectedNamespaceCredentials validCredentials = getValidCredentials();
        ProtectedNamespaceCredentials invalidCredentials = getInvalidCredentials();

        assertTrue(nsRegistry.hasWriteAccess("http://opentravel.org/test/", validCredentials));
        assertTrue(nsRegistry.hasWriteAccess("http://opentravel.org/test/project/v01",
                validCredentials));
        assertTrue(nsRegistry.hasWriteAccess("http://www.sabre-holdings.com/STL/v02",
                validCredentials));

        assertFalse(nsRegistry.hasWriteAccess("http://opentravel.org/test/", invalidCredentials));
        assertFalse(nsRegistry.hasWriteAccess("http://opentravel.org/test/project/v01",
                invalidCredentials));
        assertTrue(nsRegistry.hasWriteAccess("http://www.sabre-holdings.com/STL/v02",
                invalidCredentials));
    }

    private ProtectedNamespaceRegistry newProtectedNamespaceRegistry() throws Exception {
        DefaultProtectedNamespaceRegistry nsRegistry = new DefaultProtectedNamespaceRegistry();
        List<ProtectedNamespaceGroup> nsGroups = new ArrayList<ProtectedNamespaceGroup>();

        nsGroups.add(newProtectedNamespaceGroup("OTA"));
        nsRegistry.setProtectedNamespaces(nsGroups);
        return nsRegistry;
    }

    private ProtectedNamespaceGroup newProtectedNamespaceGroup(String groupId) throws Exception {
        File credentialsFile = new File(System.getProperty("user.dir"),
                "/src/test/resources/security/otm-credentials.otc");
        DefaultProtectedNamespaceGroup nsGroup = new DefaultProtectedNamespaceGroup();
        List<String> nsList = new ArrayList<String>();

        nsGroup.setGroupId(groupId);
        nsGroup.setCredentialUrl(URLUtils.toURL(credentialsFile));
        nsGroup.setProtectedNamespaceUris(nsList);

        nsList.add("http://opentravel.org/common");
        nsList.add("http://opentravel.org/test");

        return nsGroup;
    }

    private ProtectedNamespaceCredentials getValidCredentials() {
        ProtectedNamespaceCredentials credentials = new ProtectedNamespaceCredentials();

        credentials.setCredentials("OTA", "admin", "password");
        return credentials;
    }

    private ProtectedNamespaceCredentials getInvalidCredentials() {
        ProtectedNamespaceCredentials credentials = new ProtectedNamespaceCredentials();

        credentials.setCredentials("OTA", "admin", "bad_password");
        return credentials;
    }

}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.plugins.pmd.stubs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.ReportPlugin;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @author <a href="mailto:oching@apache.org">Maria Odea Ching</a>
 * @version $Id$
 */
public class DefaultConfigurationMavenProjectStub extends PmdProjectStub {
    private List<ReportPlugin> reportPlugins = new ArrayList<>();

    public DefaultConfigurationMavenProjectStub() throws XmlPullParserException, IOException {
        super("/def/configuration");
    }

    @Override
    public File getBasedir() {
        return new File(super.getBasedir() + "/default-configuration");
    }

    @Override
    protected String getPOM() {
        return "default-configuration-plugin-config.xml";
    }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.portals.bridges.groovy;

import java.io.IOException;

import javax.portlet.PortletException;

import org.codehaus.groovy.control.CompilationFailedException;

import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;

/**
 * <p>
 * GroovyPortletHeaderPhaseSupport parses and invokes a groovy-scripted portlet. A groovy-scripted
 * portlet just need to be implemented like any other Java-based portlet. So, a
 * groovy-scripted portlet does not support only full features of JSR-168 portlet, but
 * it also supports JSR-286 header phase.
 * 
 * @author <a href="mailto:woon_san@yahoo.com">Woonsan Ko</a>
 * @Id@
 */
public class GroovyPortletHeaderPhaseSupport extends GroovyPortlet implements SupportsHeaderPhase
{
    protected SupportsHeaderPhase scriptPortletInstanceWithHeaderPhase;
    
    public GroovyPortletHeaderPhaseSupport()
    {
        super();
    }
    
    public void doHeader(PortletHeaderRequest request, PortletHeaderResponse response) throws PortletException
    {
        refreshPortletInstance();
        
        if (this.scriptPortletInstanceWithHeaderPhase != null)
        {
            this.scriptPortletInstanceWithHeaderPhase.doHeader(request, response);
        }
    }
    
    protected void createScriptPortletInstance() throws CompilationFailedException, InstantiationException,
            IOException, IllegalAccessException, PortletException
    {
        super.createScriptPortletInstance();
        
        if (scriptPortletInstance instanceof SupportsHeaderPhase)
        {
            this.scriptPortletInstanceWithHeaderPhase = (SupportsHeaderPhase) scriptPortletInstance;
        }
    }
}

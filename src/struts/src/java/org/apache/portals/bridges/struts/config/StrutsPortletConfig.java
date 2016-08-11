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
package org.apache.portals.bridges.struts.config;

import java.io.InputStream;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;

import org.apache.commons.digester.Digester;

public class StrutsPortletConfig
{
    private RenderContextAttributes renderContextAttributes;
    private PortletURLTypes portletURLTypes;
    
    public void loadConfig(PortletContext portletContext,String config) throws PortletException
    {        
        renderContextAttributes = new RenderContextAttributes();
        portletURLTypes = new PortletURLTypes();
        
        InputStream input = portletContext.getResourceAsStream(config);
        if (input == null)
        {
            return;
        }
        
        Digester digester = new Digester();
        digester.setClassLoader(Thread.currentThread().getContextClassLoader());

        renderContextAttributes.configure(digester);
        portletURLTypes.configure(digester);
        
        try
        {
            digester.parse(input);            
        }
        catch (Exception e)
        {
            throw new PortletException("Error loading StrutsPortlet config " + config + ": " + e.getMessage(), e);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (Exception e)
            {
            }
        }
    }
    
    public RenderContextAttributes getRenderContextAttributes()
    {
        return renderContextAttributes;
    }

    public PortletURLTypes getPortletURLTypes()
    {
        return portletURLTypes;
    }
}

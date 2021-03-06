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
package org.apache.portals.bridges.php;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.portlet.PortletConfig;

/**
* ServletConfigImpl Wrapper around PortletConfig
* 
* @author <a href="mailto:rogerrut@apache.org">Roger Ruttimann</a>
* @version $Id: ServletConfigImpl.java 545680 2007-06-09 01:48:26Z ate $
*/
class ServletConfigImpl implements ServletConfig
{
    private PortletConfig config;
    private ServletContext context = null;
    public ServletConfigImpl(PortletConfig config)
    {
        this.config = config;
    }
    public ServletConfigImpl(PortletConfig config, ServletContext context)
    {
        this.config = config;
        this.context = context;
    }
    public String getInitParameter(String arg0)
    {
        return config.getInitParameter(arg0);
    }
    public Enumeration getInitParameterNames()
    {
        return config.getInitParameterNames();
    }
    public synchronized ServletContext getServletContext()
    {
        
        return context;
    }
    public String getServletName()
    {
        return config.getPortletName();
    }
    public String toString()
    {
        return config.toString();
    }
}
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
package org.apache.portals.bridges.struts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.TilesRequestProcessor;

/**
 * PortletTilesRequestProcessor
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletTilesRequestProcessor.java 517068 2007-03-12 01:44:37Z ate $
 */
public class PortletTilesRequestProcessor extends TilesRequestProcessor 
{
    public PortletTilesRequestProcessor()
    {
        super();
    }

    public void process(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException
    {
        if (PortletServlet.isPortletRequest(request))
        {
            if (!(response instanceof PortletServletResponseWrapper))
            {
                response = new PortletServletResponseWrapper(request, response);
            }
        }
        super.process(request, response);
    }
    protected boolean processRoles(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping)
            throws IOException, ServletException
    {
        boolean proceed = super.processRoles(request, response, mapping);
        if (proceed &&
                PortletServlet.isPortletRequest(request) &&
                ((PortletServlet) super.servlet).performActionRenderRequest(request, response, mapping))
        {
            return false;
        } else
            return proceed;
    }
}

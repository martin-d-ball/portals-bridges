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
package org.apache.portals.bridges.portletfilter;


import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * A filter is an object that performs filtering tasks on either the request
 * to a resource (a portlet), or on the response from a resource, or both.
 * 
 * Filters perform filtering in the renderFilter and processActionFilter 
 * method. Every Filter has access to a PortletFilterConfig object from 
 * which it can obtain its initialization parameters, a reference to the 
 * PortletConfig which it can use, for example, to load resources needed 
 * for filtering tasks. Filters are configured in the deployment descriptor 
 * of a portlet(portlet.xml).
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 */
public interface PortletFilter
{

    /**
     * Called by init method of FilterPortlet to initialize this 
     * portlet filter.
     * 
     * @param filterConfig
     * @throws PortletException
     */
    public abstract void init(PortletFilterConfig filterConfig) throws PortletException;

    /**
     * Called by render method of FilterPortlet to put tags, such as 
     * &lt;style&gt;, into &lt;head&gt;.
     * 
     * @param request
     * @param response
     * @param chain PortletFilterChain instance
     * @throws PortletException
     */
    public abstract void renderFilter(RenderRequest request, RenderResponse response, PortletFilterChain chain)
            throws PortletException, IOException;

    /**
     * Called by render method of FilterPortlet to wrap the request 
     * when it has a multipart content.
     * 
     * @param request
     * @param response
     * @param chain PortletFilterChain instance
     * @throws PortletException
     */
    public abstract void processActionFilter(ActionRequest request, ActionResponse response, PortletFilterChain chain)
            throws PortletException, IOException;

    /**
     * Called by destroy method of FilterPortlet to destroy this 
     * portlet filter.
     * 
     * @throws PortletException
     */
    public abstract void destroy();

}
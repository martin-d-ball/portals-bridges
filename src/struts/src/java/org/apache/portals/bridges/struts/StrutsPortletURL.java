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

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * StrutsPortletURL
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: StrutsPortletURL.java 517068 2007-03-12 01:44:37Z ate $
 */
public class StrutsPortletURL
{
    public static final String PAGE = "_spage";
    public static final String ORIGIN = "_sorig";
    public static final String KEEP_RENDER_ATTRIBUTES = "_kra";
    
    public static String getPageURL(ServletRequest request)
    {
        return (String)request.getAttribute(StrutsPortlet.PAGE_URL);
    }
    public static String getOriginURL(ServletRequest request)
    {
        return (String)request.getAttribute(StrutsPortlet.ORIGIN_URL);
    }
    private static PortletURL createPortletURL(ServletRequest request,
            String pageURL, boolean actionURL)
    {
        RenderResponse renderResponse = (RenderResponse) request
                .getAttribute("javax.portlet.response");
        PortletURL portletURL;
        if (actionURL)
            portletURL = renderResponse.createActionURL();
        else
            portletURL = renderResponse.createRenderURL();
        if (request instanceof HttpServletRequest)
        {
            String contextPath = ((HttpServletRequest) request)
                    .getContextPath();
            if (pageURL.startsWith(contextPath))
                pageURL = pageURL.substring(contextPath.length());
        }
        if (actionURL)
        {
            portletURL.setParameter(PAGE, pageURL.replaceAll("&amp;","&"));
            String originURL = request.getParameter(PAGE);
            if (originURL != null)
                portletURL.setParameter(ORIGIN, originURL);
        }
        else
        {
            RenderRequest renderRequest = (RenderRequest)request.getAttribute("javax.portlet.request");
            portletURL.setParameter(PAGE+renderRequest.getPortletMode().toString(), pageURL.replaceAll("&amp;","&"));
        }
        return portletURL;
    }
    public static PortletURL createRenderURL(ServletRequest request,
            String pageURL)
    {
        return createPortletURL(request, pageURL, false);
    }
    public static PortletURL createActionURL(ServletRequest request,
            String pageURL)
    {
        return createPortletURL(request, pageURL, true);
    }
}
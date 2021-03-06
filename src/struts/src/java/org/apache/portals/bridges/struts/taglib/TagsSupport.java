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
package org.apache.portals.bridges.struts.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.portals.bridges.struts.PortletServlet;
import org.apache.portals.bridges.struts.StrutsPortlet;
import org.apache.portals.bridges.struts.StrutsPortletURL;
import org.apache.portals.bridges.struts.config.StrutsPortletConfig;
import org.apache.portals.bridges.struts.config.PortletURLTypes; // javadoc

/**
 * Utility class providing common Struts Bridge Tags functionality. 
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: TagsSupport.java 517068 2007-03-12 01:44:37Z ate $
 */
class TagsSupport
{
    /**
     * Private constructor as this class isn't supposed to be instantiated.
     *
     */
    private TagsSupport(){}
    
    /**
     * Resolves a, possibly relative, url to a context relative one for use within a Portlet context.
     * <p>
     * The url parameter may contain relative (../) elements.
     * </p>
     * @param pageContext the JSP pageContext
     * @param url the url to resolve
     * @return the resolved url
     */
    public static String getContextRelativeURL(PageContext pageContext, String url, boolean addContextPath)
    {
        if ( !url.startsWith("/"))
        {
            String newUrl = url;
            String currentPath = (String)pageContext.getRequest().getAttribute(StrutsPortlet.PAGE_URL);
            if ( addContextPath )
            {
                currentPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath() + currentPath;
            }
            if ( addContextPath || currentPath.length() > 1 /* keep "/" */)
            {
                currentPath = currentPath.substring(0,currentPath.lastIndexOf('/'));
            }
            if ( currentPath.length() == 0 )
            {
                currentPath = "/";
            }
            while ( currentPath.length() > 0 )
            {
                if ( newUrl.startsWith("../"))
                {
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'));
                    newUrl = newUrl.substring(3);
                }
                else
                {
                    break;
                }
            }
            if ( currentPath.length() > 1 )
            {
                url = currentPath + "/" + newUrl;
            }
            else
            {
                url = "/" + newUrl;
            }
        }
        return url;
    }
    
    /**
     * Creates an action or render PortletURL, or a ResourceURL.
     * <p>
     * The url parameter is first {@link #getContextRelativeURL(PageContext, String) resolved}
     * to an context relative url.<br/>
     * Then, a prefixed contextPath is removed from the resulting url.<br/>
     * If the type parameter is specified (not null), the type of url created is based on its value.<br/>
     * Otherwise, {@link PortletURLTypes#getType(String)} is used to determine which
     * type of url must be created.
     * </p>
     * @param pageContext the JSP pageContext
     * @param url the url to resolve
     * @param type indicated which type of url must be created
     * @return an action or render PortletURL, or a ResourceURL
     */
    public static String getURL(PageContext pageContext, String url, PortletURLTypes.URLType type)
    {
        url = getContextRelativeURL(pageContext,url,false);
        String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
        if (url.startsWith(contextPath + "/"))
        {
            url = url.substring(contextPath.length());
        }
        
        if ( type == null )
        {
            StrutsPortletConfig strutsPortletConfig = (StrutsPortletConfig)pageContext.getAttribute(StrutsPortlet.STRUTS_PORTLET_CONFIG,PageContext.APPLICATION_SCOPE);
            type = strutsPortletConfig.getPortletURLTypes().getType(url);
        }
        
        if ( type.equals(PortletURLTypes.URLType.ACTION) )
        {
            return StrutsPortletURL.createActionURL(pageContext.getRequest(),url).toString();
        }
        else if ( type.equals(PortletURLTypes.URLType.RENDER) )
        {
            return StrutsPortletURL.createRenderURL(pageContext.getRequest(),url).toString();
        }        
        else // type.equals(PortletURLTypes.URLType.RESOURCE)
        {
            if ( url.startsWith("/"))
            {
                return contextPath + url;
            }
            return contextPath + "/" + url;
        }        
    }

    /**
     * Replaces the action url as generated by the struts:form tag with an action PortletURL.
     * @param pageContext the JSP pageContext
     * @param formStartElement the formStartElement as generated by the struts:form tag
     * @return the formStartElement containing an action PortletURL
     */
    public static String getFormTagRenderFormStartElement(PageContext pageContext, String formStartElement)
    {
        if ( PortletServlet.isPortletRequest(pageContext.getRequest()))
        {
            int actionURLStart = formStartElement.indexOf("action=") + 8;
            int actionURLEnd = formStartElement.indexOf('"', actionURLStart);
            String actionURL = formStartElement.substring(actionURLStart,
                    actionURLEnd);
            formStartElement = formStartElement.substring(0, actionURLStart)
                    + StrutsPortletURL.createActionURL(pageContext.getRequest(),
                            actionURL).toString()
                    + formStartElement.substring(actionURLEnd);
        }
        return formStartElement;        
    }    
}

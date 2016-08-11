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
package org.apache.portals.bridges.util;

import java.util.Enumeration;

import javax.portlet.PortletSession;
import javax.portlet.PortletSessionUtil;

/**
 * PortletWindowUtils
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletWindowUtils.java 517068 2007-03-12 01:44:37Z ate $
 */
public class PortletWindowUtils
{
    public static String PORTLET_WINDOW_ID = "org.apache.portals.bridges.util.portlet_window_id";
    
    /**
     * Return the unique identification for the portlet window as assigned by the portal/portlet-container.
     * <br/>
     * This method makes use of the PortletSession to determine the window id as specified by the Portlet Specification 1.0, PLT.15.3,
     * as well as stores the determined value under the {@link #PORTLET_WINDOW_ID} in the portlet scope session.
     * 
     * @param session the current PortletSession
     * @return the unique identification of the portlet window
     */
    public static String getPortletWindowId(PortletSession session)
    {
        String portletWindowId = (String)session.getAttribute(PORTLET_WINDOW_ID);
        if ( portletWindowId == null )
        {
            synchronized (session)
            {
                Double value = new Double(Math.random());
                session.setAttribute(PORTLET_WINDOW_ID, value);
                Enumeration names = session.getAttributeNames(PortletSession.APPLICATION_SCOPE);
                while (names.hasMoreElements())
                {
                    String name = (String)names.nextElement();
                    if (PortletSessionUtil.decodeAttributeName(name).equals(PORTLET_WINDOW_ID) && value.equals(session.getAttribute(name,PortletSession.APPLICATION_SCOPE)) )
                    {
                        portletWindowId = name.substring("javax.portlet.p.".length(),name.indexOf('?'));
                        session.setAttribute(PORTLET_WINDOW_ID, portletWindowId);
                        break;
                    }                    
                }
            }
        }
        return portletWindowId;
    }
    
    /**
     * Returns the name an attribute is (or will be) encoded in the PortletSession APPLICATION_SCOPE.
     * @param session PortletSession
     * @param attributeName the attribute name to encode
     */
    public static String getApplicationScopeSessionAttributeName(PortletSession session, String attributeName)
    {
        return getApplicationScopeSessionAttributeName(getPortletWindowId(session),attributeName);
    }

    /**
     * Returns the name an attribute is (or will be) encoded in the PortletSession APPLICATION_SCOPE.
     * 
     * @param portletWindowId the unique portlet window identification retrieved from {@link #getPortletWindowId(PortletSession)}.
     * @param attributeName the attribute name to encode
     */
    public static String getApplicationScopeSessionAttributeName(String portletWindowId, String attributeName)
    {
        return "javax.portlet.p."+portletWindowId+"?"+attributeName;
    }
}

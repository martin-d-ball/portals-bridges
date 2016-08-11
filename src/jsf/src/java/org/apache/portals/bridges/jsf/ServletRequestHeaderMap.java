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
package org.apache.portals.bridges.jsf;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * <p>
 * This must be the set of properties available via the javax.portlet.PortletRequest methods getProperty()
 * and getPropertyNames(). As such, HTTP headers will only be included if they were provided by the portlet container,
 * and additional properties provided by the portlet container may also be included.
 * @author Apache MyFaces team
 */
public class ServletRequestHeaderMap extends AbstractAttributeMap
{
    /** The portlet request. */
    private final HttpServletRequest servletRequest;

    /**
     * @param portletRequest The {@link javax.portlet.PortletRequest}.
     */
    ServletRequestHeaderMap(HttpServletRequest portletRequest)
    {
        this.servletRequest = portletRequest;
    }

    /**
     * @see AbstractAttributeMap#getAttribute(String)
     */
    protected Object getAttribute(String key)
    {
        return servletRequest.getHeader(key);
    }

    /**
     * @see AbstractAttributeMap#setAttribute(String, Object)
     */
    protected void setAttribute(String key, Object value)
    {
        throw new UnsupportedOperationException(
            "Cannot set PortletRequest Property");
    }

    /**
     * @see AbstractAttributeMap#removeAttribute(String)
     */
    protected void removeAttribute(String key)
    {
        throw new UnsupportedOperationException(
            "Cannot remove PortletRequest Property");
    }

    /**
     * @see AbstractAttributeMap#getAttributeNames()
     */
    protected Enumeration getAttributeNames()
    {
        return servletRequest.getHeaderNames();
    }
}

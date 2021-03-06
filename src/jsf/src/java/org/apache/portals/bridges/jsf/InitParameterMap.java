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

import java.util.Enumeration;

import javax.portlet.PortletContext;

import org.apache.portals.bridges.jsf.AbstractAttributeMap;

/**
 * <p>
 * {@link PortletContext}init parameters as Map.
 * </p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class InitParameterMap extends AbstractAttributeMap
{
    /** Illegal argument exception message. */
    final private static String ILLEGAL_ARGUMENT = "Only PortletContext supported";

    /** The {@link PortletContext}. */
    final private PortletContext portletContext;

    /**
     * @param context The context.
     */
    public InitParameterMap(Object context)
    {
        if (context instanceof PortletContext)
        {
            this.portletContext = (PortletContext) context;
        }
        else
        {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#getAttribute(java.lang.String)
     */
    public Object getAttribute(String key)
    {
        if (null != this.portletContext)
        {
            return this.portletContext.getInitParameter(key);
        }
        else
        {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#setAttribute(java.lang.String,
     *      java.lang.Object)
     */
    public void setAttribute(String key, Object value)
    {
        throw new UnsupportedOperationException("Cannot set PortletContext InitParameter");
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String key)
    {
        throw new UnsupportedOperationException("Cannot remove PortletContext InitParameter");
    }

    /**
     * @see org.apache.portals.bridges.jsf.AbstractAttributeMap#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
        if (null != this.portletContext)
        {
            return this.portletContext.getInitParameterNames();
        }
        else
        {
            throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }
}
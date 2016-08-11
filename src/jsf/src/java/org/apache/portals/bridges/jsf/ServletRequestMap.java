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

import javax.servlet.ServletRequest;
import java.util.Enumeration;

/**
 * <p>{@link javax.portlet.PortletRequest} attributes Map.</p>
 * <p>
 * See MyFaces project for servlet implementation.
 * </p>
 *
 * @author <a href="tomsp@apache.org">Thomas Spiegl</a>
 */
public class ServletRequestMap extends AbstractAttributeMap
{
	/** Illegal argument exception message. */
	final private static String ILLEGAL_ARGUMENT = "Only ServletREquest supported";
	/** The {@link javax.portlet.PortletContext}. */
	private final ServletRequest servletRequest;

    /**
     * @param request The request.
     */
    public ServletRequestMap(Object request)
    {
        if (request instanceof ServletRequest)
        {
        	this.servletRequest = (ServletRequest) request;
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see AbstractAttributeMap#getAttribute(String)
     */
    public Object getAttribute(String key)
    {
        if (null != this.servletRequest)
        {
        	return this.servletRequest.getAttribute(key);
        }
        else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }

    /**
     * @see AbstractAttributeMap#setAttribute(String, Object)
     */
    public void setAttribute(String key, Object value)
    {
    	if (null != this.servletRequest)
        {
    		this.servletRequest.setAttribute(key, value);
        }
    }

    /**
     * @see AbstractAttributeMap#removeAttribute(String)
     */
    public void removeAttribute(String key)
    {
    	if (null != this.servletRequest)
        {
    		this.servletRequest.removeAttribute(key);
        }
    }

    /**
     * @see AbstractAttributeMap#getAttributeNames()
     */
    public Enumeration getAttributeNames()
    {
    	if (null != this.servletRequest)
        {
    		return this.servletRequest.getAttributeNames();
        }
    	else
        {
        	throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
        }
    }
}

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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <p>
 * Loads the {@link PortletFacesContextImpl}
 * </p>
 *
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class FacesContextFactoryImpl extends FacesContextFactory
{
    /**
     * @see javax.faces.context.FacesContextFactory#getFacesContext(java.lang.Object,
     *      java.lang.Object, java.lang.Object, javax.faces.lifecycle.Lifecycle)
     */
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
            throws FacesException
    {
        if (context instanceof PortletContext)
        {
            return new PortletFacesContextImpl(
                    (PortletContext) context,
                    (PortletRequest) request,
                    (PortletResponse) response);
        }
        else if (context instanceof ServletContext)
        {
            return new ServletFacesContextImpl(
                    (ServletContext) context,
                    (ServletRequest) request,
                    (ServletResponse) response);
        }
        else
        {
            throw new FacesException("Unsupported context type " + context.getClass().getName());
        }
    }
}
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
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FilterPortlet provides a portlet implementation to filter the 
 * specified portlet. The filtered portlet and filters are defined in 
 * a portlet descriptor(portlet.xml).  The filetered portlet is specified
 * by portlet-class, and the filters are specified by portlet-filters.
 * 
 * Example:
 * <pre>
 * &lt;portlet-app id="example-portlets" version="1.0"&gt;
 *    &lt;portlet id="ExamplePortlet"&gt;
 * ...
 *        &lt;init-param&gt;
 *            &lt;name&gt;portlet-class&lt;/name&gt;
 *            &lt;value&gt;org.apache.myfaces.portlet.MyFacesGenericPortlet&lt;/value&gt;
 *        &lt;/init-param&gt;
 *        &lt;init-param&gt;
 *            &lt;name&gt;portlet-filters&lt;/name&gt;
 *            &lt;value&gt;org.apache.myfaces.portlet.TomahawkPortletFilter&lt;/value&gt;
 *        &lt;/init-param&gt;
 *        &lt;init-param&gt;
 *            &lt;name&gt;org.apache.myfaces.portlet.TomahawkPortletFilter:upload-threshold-size&lt;/name&gt;
 *            &lt;value&gt;1m&lt;/value&gt;
 *        &lt;/init-param&gt;
 *        &lt;init-param&gt;
 *            &lt;name&gt;org.apache.myfaces.portlet.TomahawkPortletFilter:upload-max-file-size&lt;/name&gt;
 *            &lt;value&gt;10m&lt;/value&gt;
 *        &lt;/init-param&gt;
 * ...
 * </pre>
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 */
public class FilterPortlet extends GenericPortlet
{
    private static final Log log = LogFactory.getLog(FilterPortlet.class);

    public static final String PORTLET_CLASS = "portlet-class";

    private PortletFilterChain portletFilterChain;

    private Portlet portlet;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        if (log.isTraceEnabled())
        {
            log.trace("Initializing FilterPortlet.");
        }

        // create Portlet
        String portletClassName = config.getInitParameter(PORTLET_CLASS);
        if (portletClassName == null)
        {
            throw new PortletException("Portlet Class Name is null.");
        }

        // create PortletFilterChain
        portletFilterChain = new PortletFilterChain(config);

        portlet = null;
        try
        {
            Class portletClass = Class.forName(portletClassName);
            Object portletObj = portletClass.newInstance();
            if (portletObj instanceof Portlet)
            {
                portlet = (Portlet) portletObj;
                portlet.init(config);
            }
            else
            {
                throw new PortletException(portletClassName + " is not Portlet instance.");
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new PortletException("Class " + portletClassName + " is not found.", e);
        }
        catch (InstantiationException e)
        {
            throw new PortletException("Could not instantiate " + portletClassName + ".", e);
        }
        catch (IllegalAccessException e)
        {
            throw new PortletException("Illegal Access: " + portletClassName, e);
        }

        portletFilterChain.setPortlet(portlet);
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("called processAction method.");
        }
        portletFilterChain.reset();
        portletFilterChain.processActionFilter(request, response);
    }

    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        if (log.isTraceEnabled())
        {
            log.trace("called render method.");
        }
        portletFilterChain.reset();
        portletFilterChain.renderFilter(request, response);
    }

    public void destroy()
    {
        if (log.isTraceEnabled())
        {
            log.trace("called destory method.");
        }
        portletFilterChain.release();
        portlet.destroy();
        portlet = null;
        portletFilterChain = null;
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletConfig#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String arg0)
    {
        if (portlet instanceof PortletConfig)
        {
            return ((PortletConfig) portlet).getInitParameter(arg0);
        }
        return super.getInitParameter(arg0);
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletConfig#getInitParameterNames()
     */
    public Enumeration getInitParameterNames()
    {
        if (portlet instanceof PortletConfig)
        {
            return ((PortletConfig) portlet).getInitParameterNames();
        }
        return super.getInitParameterNames();
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletConfig#getPortletContext()
     */
    public PortletContext getPortletContext()
    {
        if (portlet instanceof PortletConfig)
        {
            return ((PortletConfig) portlet).getPortletContext();
        }
        return super.getPortletContext();
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletConfig#getPortletName()
     */
    public String getPortletName()
    {
        if (portlet instanceof PortletConfig)
        {
            return ((PortletConfig) portlet).getPortletName();
        }
        return super.getPortletName();
    }

    /* (non-Javadoc)
     * @see javax.portlet.PortletConfig#getResourceBundle(java.util.Locale)
     */
    public ResourceBundle getResourceBundle(Locale arg0)
    {
        if (portlet instanceof PortletConfig)
        {
            return ((PortletConfig) portlet).getResourceBundle(arg0);
        }
        return super.getResourceBundle(arg0);
    }
}

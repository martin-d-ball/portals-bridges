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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

/**
 * A filter configuration object used by FilterPortlet to pass 
 * information to a filter during initialization.
 *
 * The initialization parameter provided by getInitParameter(String) is 
 * specified in the portlet descriptor(portlet.xml) with the target 
 * PortletFilter name and a separator(:). 
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
 *
 */
public class PortletFilterConfig
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory.getLog(PortletFilterConfig.class);

    private String PARAMETER_SEPRATOR = ":";

    private PortletFilter portletFilter = null;

    private PortletConfig portletConfig;

    private String filterName;

    private Map parameters;

    public PortletFilterConfig(String filterName, PortletConfig config) throws PortletException
    {
        setPortletConfig(config);
        setFilterName(filterName);
        parseParameters();

        try
        {
            Class portletFilterClass = Class.forName(filterName);
            if (portletFilterClass != null)
            {
                Object portletFilter = portletFilterClass.newInstance();
                if (portletFilter instanceof PortletFilter)
                {
                    this.portletFilter = (PortletFilter) portletFilter;
                }
                else
                {
                    throw new PortletException(filterName + " is not PortletFilter class.");
                }
            }
            else
            {
                throw new PortletException(filterName + " is not found.");
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new PortletException("ClassNotFoundException occurred.", e);
        }
        catch (InstantiationException e)
        {
            throw new PortletException("InstantiationException occurred.", e);
        }
        catch (IllegalAccessException e)
        {
            throw new PortletException("IllegalAccessException occurred.", e);
        }

        // init
        this.portletFilter.init(this);
    }

    /**
     * Parses initialization parameters in a portlet descriptor(portlet.xml).
     */
    private void parseParameters()
    {
        parameters = new HashMap();
        for (Enumeration e = getPortletConfig().getInitParameterNames(); e.hasMoreElements();)
        {
            String key = (String) e.nextElement();
            if (key.startsWith(getFilterName() + PARAMETER_SEPRATOR))
            {
                String newKey = key.substring(getFilterName().length() + PARAMETER_SEPRATOR.length());
                parameters.put(newKey, getPortletConfig().getInitParameter(key));
            }
        }
    }

    /**
     * Return a <code>String</code> containing the value of the named initialization parameter, or <code>null</code>
     * if the parameter does not exist.
     * 
     * @param name Name of the requested initialization parameter
     */
    public String getInitParameter(String name)
    {
        if (parameters == null)
        {
            return (null);
        }
        else
        {
            return ((String) parameters.get(name));
        }
    }

    /**
     * Return an <code>Enumeration</code> of the names of the initialization parameters for this Filter.
     */
    public Enumeration getInitParameterNames()
    {
        if (parameters == null)
            return (new Enumerator(new ArrayList().iterator()));
        else
            return (new Enumerator(parameters.keySet().iterator()));

    }

    /**
     * Returns the PortletFilter instance.
     * 
     * @return
     */
    public PortletFilter getPortletFilter()
    {
        return portletFilter;
    }

    /**
     * @return Returns the portletConfig.
     */
    public PortletConfig getPortletConfig()
    {
        return portletConfig;
    }

    /**
     * @param portletConfig The portletConfig to set.
     */
    public void setPortletConfig(PortletConfig portletConfig)
    {
        this.portletConfig = portletConfig;
    }

    public void release()
    {
        portletFilter.destroy();
        portletConfig = null;
    }

    /**
     * @return Returns the filterName.
     */
    public String getFilterName()
    {
        return filterName;
    }

    /**
     * @param filterName The filterName to set.
     */
    public void setFilterName(String filterName)
    {
        this.filterName = filterName;
    }

    /**
     * Uitlity class to wraps an <code>Iterator</code>
     * 
     * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
     *
     */
    public final class Enumerator implements Enumeration
    {
        /**
         * Return an Enumeration over the values returned by the specified 
         * Iterator.
         * 
         * @param iterator Iterator to be wrapped
         */
        public Enumerator(Iterator iterator)
        {

            super();
            this.iterator = iterator;

        }

        /**
         * The <code>Iterator</code> over which the <code>Enumeration</code> 
         * represented by this class actually operates.
         */
        private Iterator iterator = null;

        /**
         * Tests if this enumeration contains more elements.
         * 
         * @return <code>true</code> if and only if this enumeration object 
         *                           contains at least one more element to
         *                           provide, <code>false</code> otherwise
         */
        public boolean hasMoreElements()
        {

            return (iterator.hasNext());

        }

        /**
         * Returns the next element of this enumeration if this enumeration 
         * has at least one more element to provide.
         * 
         * @return the next element of this enumeration
         * @exception NoSuchElementException if no more elements exist
         */
        public Object nextElement() throws NoSuchElementException
        {

            return (iterator.next());

        }

    }
}

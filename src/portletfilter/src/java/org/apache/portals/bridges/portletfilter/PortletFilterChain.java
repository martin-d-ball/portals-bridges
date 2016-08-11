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
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A PortletFilterChain is an object provided to the developer giving a view 
 * into the invocation chain of a filtered request for a resource. 
 * PortletFilters use the PortletFilterChain to invoke the next filter in the 
 * chain, or if the calling filter is the last filter in the chain, to invoke 
 * the resource at the end of the chain.
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 *
 */
public class PortletFilterChain
{
    private static final Log log = LogFactory.getLog(PortletFilterChain.class);

    // -------------------------------------------------------------- Constants

    public static final int INCREMENT = 10;

    public static final String PORTLET_FILTERS = "portlet-filters";

    /**
     * PortletFilters.
     */
    private PortletFilterConfig[] filters = new PortletFilterConfig[0];

    /**
     * The int which is used to maintain the current position in the filter chain.
     */
    private ThreadLocal renderPosition = new ThreadLocal();

    /**
     * The int which is used to maintain the current position in the filter chain.
     */
    private ThreadLocal processActionPosition = new ThreadLocal();

    /**
     * The int which gives the current number of filters in the chain.
     */
    private int n = 0;

    /**
     * The portlet instance to be executed by this chain.
     */
    private Portlet portlet = null;

    public PortletFilterChain(PortletConfig config)
    {
        String portletFilters = config.getInitParameter(PORTLET_FILTERS);
        StringTokenizer st = new StringTokenizer(portletFilters, ", ");
        while (st.hasMoreTokens())
        {
            String className = st.nextToken();
            try
            {
                addPortletFilter(new PortletFilterConfig(className, config));
            }
            catch (PortletException e)
            {
                log.warn("Invalid portlet filter: " + className, e);
            }
        }
    }

    /**
     * Causes the next filter for renderFilter in the chain to be invoked, or 
     * if the calling filter is the last filter in the chain, causes the 
     * resource at the end of the chain to be invoked.
     * 
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void renderFilter(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        // Call the next filter if there is one
        int pos = ((Integer) renderPosition.get()).intValue();
        if (pos < n)
        {
            PortletFilterConfig filterConfig = filters[pos++];
            PortletFilter filter = filterConfig.getPortletFilter();
            renderPosition.set(new Integer(pos));

            filter.renderFilter(request, response, this);

            renderPosition.set(new Integer(--pos));
            return;
        }

        // We fell off the end of the chain -- call the servlet instance
        portlet.render(request, response);
    }

    /**
     * Causes the next filter for processActionFilter in the chain to be invoked, or 
     * if the calling filter is the last filter in the chain, causes the 
     * resource at the end of the chain to be invoked.
     * 
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void processActionFilter(ActionRequest request,
            ActionResponse response) throws PortletException, IOException
    {
        // Call the next filter if there is one
        int pos = ((Integer) processActionPosition.get()).intValue();
        if (pos < n)
        {
            PortletFilterConfig filterConfig = filters[pos++];
            PortletFilter filter = filterConfig.getPortletFilter();
            processActionPosition.set(new Integer(pos));

            filter.processActionFilter(request, response, this);

            processActionPosition.set(new Integer(--pos));
            return;
        }

        // We fell off the end of the chain -- call the servlet instance
        portlet.processAction(request, response);

    }

    /**
     * Add a filter to the set of filters that will be executed in this chain.
     * 
     * @param filterConfig The PortletFilterConfig for the portlet to be executed
     */
    public void addPortletFilter(PortletFilterConfig filterConfig)
    {
        if (filterConfig != null && filterConfig.getPortletFilter() != null)
        {
            if (n == filters.length)
            {
                PortletFilterConfig[] newFilters = new PortletFilterConfig[n
                        + INCREMENT];
                System.arraycopy(filters, 0, newFilters, 0, n);
                filters = newFilters;
            }
            filters[n++] = filterConfig;
        }
    }

    /**
     * Reset this filter chain
     */
    public void reset()
    {
        renderPosition.set(new Integer(0));
        processActionPosition.set(new Integer(0));
    }

    /**
     * Release references to the filters and wrapper executed by this chain.
     */
    public void release()
    {
        for (int i = 0; i < n; i++)
        {
            filters[i].release();
        }
        portlet = null;
    }

    /**
     * Set Portlet instance.
     * 
     * @param portlet The portlet to set.
     */
    public void setPortlet(Portlet portlet)
    {
        this.portlet = portlet;
    }

}

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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.PlugInConfig;
import org.apache.struts.tiles.TilesPlugin;
import org.apache.struts.util.RequestUtils;

/**
 * PortletServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id: PortletServlet.java 546262 2007-06-11 20:34:49Z ate $
 */
public class PortletServlet extends ActionServlet
{
    private static final Log log = LogFactory.getLog(PortletServlet.class);

    public PortletServlet()
    {
        super();
    }

    public void init(ServletConfig config) throws ServletException
    {
        super.init(new PortletServletConfigImpl(config));
    }

    public ServletContext getServletContext()
    {
        return getServletConfig().getServletContext();
    }

    protected void initModulePlugIns(ModuleConfig config)
    throws ServletException
    {
        boolean needTilesProcessor = false;
        PlugInConfig plugInConfigs[] = config.findPlugInConfigs();
        for ( int i = 0; !needTilesProcessor && i < plugInConfigs.length; i++ )
        {
            Class pluginClass = null;
            try 
            {
                pluginClass = RequestUtils.applicationClass(plugInConfigs[i].getClassName());                    
            } 
            catch (ClassNotFoundException ex)
            {
                log.fatal("Can't load Plugin class: bad class name '"+ plugInConfigs[i].getClassName()+ "'.");
                throw new ServletException(ex);
            }
            
            if (TilesPlugin.class.isAssignableFrom(pluginClass))
            {
                needTilesProcessor = true;
            }
        }
        
        String processorClassName = config.getControllerConfig().getProcessorClass();
        Class processorClass = null;
        try 
        {
            processorClass = RequestUtils.applicationClass(processorClassName);                
        } 
        catch (ClassNotFoundException ex)
        {
            log.fatal("Can't load Plugin class: bad class name '"+ processorClass +"'.");
            throw new ServletException(ex);
        }
        
        String newProcessorClassName = null;
        
        if (needTilesProcessor && !PortletTilesRequestProcessor.class.isAssignableFrom(processorClass))
        {
            newProcessorClassName = PortletTilesRequestProcessor.class.getName();
        }
        else if (!needTilesProcessor && !PortletRequestProcessor.class.isAssignableFrom(processorClass))
        {
            newProcessorClassName = PortletRequestProcessor.class.getName();
        }
        
        if (newProcessorClassName != null )
        {
            log.warn( "Replacing RequestProcessor " +
                    processorClassName +
                    " with " +
                    newProcessorClassName +
                    " for module: " +
                    (config.getPrefix().equals("") ? "default" : config.getPrefix()) + ".");

            config.getControllerConfig().setProcessorClass(newProcessorClassName);
        }
        super.initModulePlugIns(config);
    }
    
    public boolean performActionRenderRequest(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping)
            throws IOException, ServletException
    {
        if (!request.getAttribute(StrutsPortlet.REQUEST_TYPE).equals(
                StrutsPortlet.ACTION_REQUEST))
        {
            StrutsPortletRenderContext context = (StrutsPortletRenderContext)request.getAttribute(StrutsPortlet.RENDER_CONTEXT);
        	
            if (context != null)
            {
                // only use the RENDER_CONTEXT once: if the target path is an Struts Action which might result in a subsequent
                // dispatch, the normal Struts processing should happen, otherwise a infinite loop might occurr.
                request.removeAttribute(StrutsPortlet.RENDER_CONTEXT);

                if (log.isDebugEnabled())
                {
                    log.debug("render context path: " + context.getPath());
                }
                if (context.getActionForm() != null) {
                	String attribute = mapping.getAttribute();
                	if (attribute != null) {
                	    if (log.isDebugEnabled())
                	    {
                	        log.debug("Putting form " + context.getActionForm().getClass().getName() + 
                	                " into request as " + attribute + " for mapping " + mapping.getName());
                	    }
                    	request.setAttribute(mapping.getAttribute(), context
                                .getActionForm());
                	} 
                	else if (log.isWarnEnabled())
                	{
                	    log.warn("Attribute is null for form " + context.getActionForm().getClass().getName() + 
                	            ", won't put it into request for mapping " + mapping.getName());
                	}
                }
                if (context.isRequestCancelled())
                    request.setAttribute(Globals.CANCEL_KEY, Boolean.TRUE);
                if (context.getMessages() != null)
                    request.setAttribute(Globals.MESSAGE_KEY, context
                            .getMessages());
                if (context.getErrors() != null)
                    request
                            .setAttribute(Globals.ERROR_KEY, context
                                    .getErrors());
                RequestDispatcher dispatcher = null;
                if (context.getDispatchNamed())
                    dispatcher = getServletContext().getNamedDispatcher(
                            context.getPath());
                else
                    dispatcher = getServletContext().getRequestDispatcher(
                            context.getPath());
                dispatcher.include(request, response);
                return true;
            }
        }
        return false;
    }

    public static boolean isPortletRequest(ServletRequest request)
    {
        return request.getAttribute("javax.portlet.request") != null;
    }    
}
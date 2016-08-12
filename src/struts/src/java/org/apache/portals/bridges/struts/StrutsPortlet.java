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
import java.io.PrintWriter;
import java.util.Random;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.common.ServletContextProvider;
import org.apache.portals.bridges.struts.config.StrutsPortletConfig;
import org.apache.portals.bridges.struts.util.EmptyHttpServletResponseWrapper;
import org.apache.portals.bridges.util.ServletPortletSessionProxy;

/**
 * StrutsPortlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: StrutsPortlet.java 549654 2007-06-22 00:52:52Z ate $
 */
public class StrutsPortlet extends GenericPortlet
{
    /**
     * Name of class implementing {@link ServletContextProvider}
     */
    public static final String PARAM_SERVLET_CONTEXT_PROVIDER = "ServletContextProvider";
    /**
     * Name of portlet preference for Struts Portlet Config Location
     */
    public static final String STRUTS_PORTLET_CONFIG_LOCATION = "StrutsPortletConfigLocation";
    
    public static final String PORTLET_SCOPE_STRUTS_SESSION = "PortletScopeStrutsSession";
    /**
     * Name of portlet preference for Action page
     */
    public static final String PARAM_ACTION_PAGE = "ActionPage";
    /**
     * Name of portlet preference for Custom page
     */
    public static final String PARAM_CUSTOM_PAGE = "CustomPage";
    /**
     * Name of portlet preference for Edit page
     */
    public static final String PARAM_EDIT_PAGE = "EditPage";
    /**
     * Name of portlet preference for Edit page
     */
    public static final String PARAM_HELP_PAGE = "HelpPage";
    /**
     * Name of portlet preference for View page
     */
    public static final String PARAM_VIEW_PAGE = "ViewPage";
    /**
     * Default URL for the action page.
     */
    private String defaultActionPage = null;
    /**
     * Default URL for the custom page.
     */
    private String defaultCustomPage = null;
    /**
     * Default URL for the edit page.
     */
    private String defaultEditPage = null;
    /**
     * Default URL for the help page.
     */
    private String defaultHelpPage = null;
    /**
     * Default URL for the view page.
     */
    private String defaultViewPage = null;
    private ServletContextProvider servletContextProvider;
    private boolean portletScopeStrutsSession = false;
    private static final Log log = LogFactory.getLog(StrutsPortlet.class);
    public static final String REQUEST_TYPE = "org.apache.portals.bridges.struts.request_type";
    public static final String PAGE_URL = "org.apache.portals.bridges.struts.page_url";
    public static final String ORIGIN_URL = "org.apache.portals.bridges.struts.origin_url";
    public static final String REDIRECT_PAGE_URL = "org.apache.portals.bridges.struts.redirect_page_url";
    public static final String REDIRECT_URL = "org.apache.portals.bridges.struts.redirect_url";
    public static final String RENDER_CONTEXT = "org.apache.portals.bridges.struts.render_context";
    public static final String ERROR_CONTEXT = "org.apache.portals.bridges.struts.error_context";
    public static final String PORTLET_NAME = "org.apache.portals.bridges.struts.portlet_name";
    public static final String STRUTS_PORTLET_CONFIG = "org.apache.portals.bridges.struts.portlet_config";
    public static final String DEFAULT_STRUTS_PORTLET_CONFIG_LOCATION = "/WEB-INF/struts-portlet-config.xml";
    public static final String SERVLET_PORTLET_SESSION_PROXY = "org.apache.portals.bridges.util.servlet_portlet_session_proxy";
    public static final String SERVLET_PORTLET_APPLICATION_SESSION = "org.apache.portals.bridges.util.servlet_portlet_application_session";
    public static final String ACTION_REQUEST = "ACTION";
    public static final String VIEW_REQUEST = "VIEW";
    public static final String CUSTOM_REQUEST = "CUSTOM";
    public static final String EDIT_REQUEST = "EDIT";
    public static final String HELP_REQUEST = "HELP";
    
    private StrutsPortletConfig strutsPortletConfig;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        String contextProviderClassName = getContextProviderClassNameParameter(config);
        if (contextProviderClassName == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_SERVLET_CONTEXT_PROVIDER + " not specified");
        if (contextProviderClassName != null)
        {
            try
            {
                Class clazz = Class.forName(contextProviderClassName);
                if (clazz != null)
                {
                    Object obj = clazz.newInstance();
                    if (ServletContextProvider.class.isInstance(obj))
                    {
                        servletContextProvider = (ServletContextProvider) obj;
                    }
                    else
                        throw new PortletException("class not found");
                }
            } catch (Exception e)
            {
                if (e instanceof PortletException)
                    throw (PortletException) e;
                e.printStackTrace();
                throw new PortletException("Cannot load", e);
            }
        }
        if (servletContextProvider == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Invalid init parameter "
                    + PARAM_SERVLET_CONTEXT_PROVIDER + " value "
                    + contextProviderClassName);
        this.portletScopeStrutsSession = getPortletScopeStrutsSessionParameter(config).booleanValue();
        this.defaultActionPage = getActionPageParameter(config);
        this.defaultCustomPage = getCustomPageParameter(config);
        this.defaultEditPage = getEditPageParameter(config);
        this.defaultViewPage = getViewPageParameter(config);
        this.defaultHelpPage = getHelpPageParameter(config);
        
        if (this.defaultViewPage == null)
        {
            // A Struts Portlet is required to have at least the
            // defaultViewPage
            // defined!
            throw new PortletException(
                    "Portlet "
                            + config.getPortletName()
                            + " is incorrectly configured. No default View page is defined.");
        }
        if (defaultActionPage == null)
            defaultActionPage = defaultViewPage;
        if (defaultCustomPage == null)
            defaultCustomPage = defaultViewPage;
        if (defaultHelpPage == null)
            defaultHelpPage = defaultViewPage;
        if (defaultEditPage == null)
            defaultEditPage = defaultViewPage;
        
        strutsPortletConfig = new StrutsPortletConfig();
        String strutsPortletConfigLocation = getStrutsPortletConfigLocationParameter(config);
        if ( strutsPortletConfigLocation == null )
        {
            strutsPortletConfigLocation = DEFAULT_STRUTS_PORTLET_CONFIG_LOCATION;
        }
        strutsPortletConfig.loadConfig(config.getPortletContext(),strutsPortletConfigLocation);
        config.getPortletContext().setAttribute(STRUTS_PORTLET_CONFIG,strutsPortletConfig);
    }
    
    protected String getContextProviderClassNameParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_SERVLET_CONTEXT_PROVIDER);
    }
    
    protected ServletContextProvider getServletContextProvider()
    {
        return servletContextProvider;
    }
    
    protected ServletContext getServletContext(GenericPortlet portlet, PortletRequest request, PortletResponse response)
    {
        return getServletContextProvider().getServletContext(portlet);
    }
    
    protected HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request, PortletResponse response)
    {
        return getServletContextProvider().getHttpServletRequest(portlet, request);
    }
    
    protected HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletRequest request, PortletResponse response)
    {
        return getServletContextProvider().getHttpServletResponse(portlet, response);
    }
    
    protected String getStrutsPageURL(PortletRequest request)
    {
        if ( ACTION_REQUEST.equals(request.getAttribute(REQUEST_TYPE)))
        {
            return request.getParameter(StrutsPortletURL.PAGE);
        }
        return request.getParameter(StrutsPortletURL.PAGE+request.getPortletMode().toString());
    }
    
    protected String getStrutsOriginURL(PortletRequest request)
    {
        if ( ACTION_REQUEST.equals(request.getAttribute(REQUEST_TYPE)))
        {
            return request.getParameter(StrutsPortletURL.ORIGIN);
        }
        return request.getParameter(StrutsPortletURL.ORIGIN+request.getPortletMode().toString());
    }
    
    protected String getKeepRenderAttributes(PortletRequest request)
    {
        return request.getParameter(StrutsPortletURL.KEEP_RENDER_ATTRIBUTES+request.getPortletMode().toString());
    }
    
    protected String getActionPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_ACTION_PAGE);
    }
    
    protected String getCustomPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_CUSTOM_PAGE);
    }

    protected String getEditPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_EDIT_PAGE);
    }

    protected String getViewPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_VIEW_PAGE);
    }
    
    protected String getHelpPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_HELP_PAGE);
    }
    
    protected String getStrutsPortletConfigLocationParameter(PortletConfig config)
    {
        return config.getInitParameter(STRUTS_PORTLET_CONFIG_LOCATION);
    }
    
    protected Boolean getPortletScopeStrutsSessionParameter(PortletConfig config)
    {
        return Boolean.valueOf(config.getInitParameter(PORTLET_SCOPE_STRUTS_SESSION));
    }
    
    public void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultEditPage, StrutsPortlet.EDIT_REQUEST);
    }
    public void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultHelpPage, StrutsPortlet.HELP_REQUEST);
    }
    public void doCustom(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultCustomPage,
                StrutsPortlet.CUSTOM_REQUEST);
    }
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultViewPage, StrutsPortlet.VIEW_REQUEST);
    }
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultActionPage,
                StrutsPortlet.ACTION_REQUEST);
    }
    protected void processRequest(PortletRequest request, PortletResponse response,
            String defaultPage, String requestType) throws PortletException,
            IOException
    {
        ServletContext servletContext = getServletContext(this, request, response);
        HttpServletRequest req = getHttpServletRequest(this, request, response);
        HttpServletResponse res = getHttpServletResponse(this, request, response);
        String portletName = this.getPortletConfig().getPortletName();
        req.setAttribute(PORTLET_NAME, portletName);
        boolean actionRequest = ACTION_REQUEST.equals(requestType);
        
        // save requestType early so getStrutsPageURL and getStrutsOrigURL can determine the type
        req.setAttribute(StrutsPortlet.REQUEST_TYPE, requestType);
        
        PortletSession portletSession = request.getPortletSession();
        
        try
        {
            StrutsPortletErrorContext errorContext = (StrutsPortletErrorContext) portletSession.getAttribute(StrutsPortlet.ERROR_CONTEXT);
            if (errorContext != null)
            {
                if (!actionRequest)
                {
                    portletSession.removeAttribute(StrutsPortlet.ERROR_CONTEXT);
                    renderError(res, errorContext);
                }
                return;
            }

            String keepRenderAttributes = null;
            
            if ( !actionRequest )
            {
                keepRenderAttributes = getKeepRenderAttributes(request);
            }
            if ( keepRenderAttributes == null )
            {
                strutsPortletConfig.getRenderContextAttributes().clearAttributes(portletSession);
            }
            else
            {
                strutsPortletConfig.getRenderContextAttributes().restoreAttributes(request);
            }
                                
            String path = null;
            String pageURL = getStrutsPageURL(request);
            
            if (pageURL == null)
            {
                path = defaultPage;
            }
            else
            {
                path = pageURL;
            }
            if ( !actionRequest )
            {
                // restore possible render context from the session and store it as request attribute for the StrutsServlet to be able to find it
                StrutsPortletRenderContext renderContext = (StrutsPortletRenderContext)portletSession.getAttribute(RENDER_CONTEXT);
                if ( renderContext != null )
                {                        
                    portletSession.removeAttribute(RENDER_CONTEXT);
                    req.setAttribute(RENDER_CONTEXT, renderContext);
                }
            }

            if (log.isDebugEnabled())
                log.debug("process path: " + path + ", requestType: " + requestType);

            RequestDispatcher rd = servletContext.getRequestDispatcher(path);
            if (rd != null)
            {
                if (actionRequest)
                {
                    res = new EmptyHttpServletResponseWrapper(res);
                    
                    // http://issues.apache.org/jira/browse/PB-2:
                    // provide servlet access to the Portlet components even from 
                    // an actionRequest in extension to the JSR-168 requirement
                    // PLT.16.3.2 which (currently) only covers renderRequest
                    // servlet inclusion.
                    if ( req.getAttribute("javax.portlet.config") == null )
                    {
                        req.setAttribute("javax.portlet.config", getPortletConfig());
                    }
                    if ( req.getAttribute("javax.portlet.request") == null )
                    {
                        req.setAttribute("javax.portlet.request", request);
                    }
                    if ( req.getAttribute("javax.portlet.response") == null )
                    {
                        req.setAttribute("javax.portlet.response", response);
                    }
                    String origin = getStrutsOriginURL(request);
                    if ( origin == null )
                    {
                        origin = defaultPage;
                    }
                    request.setAttribute(StrutsPortlet.ORIGIN_URL, origin);                    
                }
                if (path != null)
                {
                    req.setAttribute(StrutsPortlet.PAGE_URL, path);
                }
                
                HttpSession proxiedSession = null;
                if ( portletScopeStrutsSession )
                {
                    proxiedSession = (HttpSession)portletSession.getAttribute(SERVLET_PORTLET_SESSION_PROXY);
                    if (proxiedSession == null)
                    {
                        proxiedSession = ServletPortletSessionProxy.createProxy(req);
                        portletSession.setAttribute(SERVLET_PORTLET_SESSION_PROXY, proxiedSession);
                    }
                }
                req.setAttribute(SERVLET_PORTLET_APPLICATION_SESSION, req.getSession());
                try
                {
                    rd.include(new PortletServletRequestWrapper(servletContext, req, proxiedSession), res);
                } 
                catch (ServletException e)
                {
                    if (log.isErrorEnabled())
                        log.error("Include exception", e);
                    errorContext = new StrutsPortletErrorContext();
                    errorContext.setError(e);
                    req.setAttribute(StrutsPortlet.ERROR_CONTEXT, errorContext);
                    if (!actionRequest)
                        renderError(res, errorContext);
                }
                if (actionRequest)
                {
                    String renderURL;
                    if (req.getAttribute(StrutsPortlet.ERROR_CONTEXT) != null)
                    {
                        pageURL = StrutsPortletURL.getOriginURL(req);
                        if ( pageURL != null )
                        {    
                          ((ActionResponse) response).setRenderParameter(StrutsPortletURL.PAGE+request.getPortletMode().toString(), pageURL);
                        }
                        if (log.isDebugEnabled())
                            log.debug("action render error context");
                        try
                        {
                            portletSession.setAttribute(StrutsPortlet.ERROR_CONTEXT,req.getAttribute(StrutsPortlet.ERROR_CONTEXT));
                        }
                        catch (IllegalStateException ise)
                        {
                            // catch Session already invalidated exception
                            // There isn't much we can do here other than
                            // redirecting the user to the start page
                        }
                    }
                    else
                    {
                        if ((renderURL = (String) req
                                .getAttribute(StrutsPortlet.REDIRECT_URL)) != null)
                        {
                            if (log.isDebugEnabled())
                                log.debug("action send redirect: " + renderURL);
                            ((ActionResponse) response).sendRedirect(renderURL);
                        } 
                        else
                        {
                            try
                            {
                                strutsPortletConfig.getRenderContextAttributes().saveAttributes(request);
                            }
                            catch (IllegalStateException ise)
                            {
                                // catch Session already invalidated exception
                                // There isn't much we can do here other than
                                // redirecting the user to the start page
                                return;
                            }
                            StrutsPortletRenderContext renderContext = (StrutsPortletRenderContext)req.getAttribute(RENDER_CONTEXT);
                            if ( renderContext != null )
                            {
                                portletSession.setAttribute(RENDER_CONTEXT, renderContext);
                            }
                            ((ActionResponse) response).setRenderParameter(
                                    StrutsPortletURL.KEEP_RENDER_ATTRIBUTES+request.getPortletMode().toString(), "1");

                            if ((renderURL = (String) req
                                    .getAttribute(StrutsPortlet.REDIRECT_PAGE_URL)) != null)
                            {
                                if (log.isDebugEnabled())
                                    log.debug("action render redirected page: "
                                            + renderURL);
                                pageURL = renderURL;
                            }
                            if (pageURL != null)
                            {
                                if (renderURL == null && log.isWarnEnabled())
                                    log.warn("Warning: Using the original action URL for render URL: " +pageURL+".\nA redirect should have been issued.");
                                ((ActionResponse) response).setRenderParameter(
                                        StrutsPortletURL.PAGE+request.getPortletMode().toString(), pageURL);
                            }
                        }
                    }
                }
            }
        } catch (IOException e)
        {
            if (log.isErrorEnabled())
                log.error("unexpected", e);
            throw e;
        }
    }
    protected void renderError(HttpServletResponse response,
            StrutsPortletErrorContext errorContext) throws IOException
    {
        PrintWriter writer = response.getWriter();
        writer.println("<div class=\"unexpected-error\">");
        writer.println("<h2 class=\"unexpected-error-heading\">Error</h2>");
        writer.println("<p class=\"unexpected-error-message\">Sorry an error occurred</p>");
        writer.println("</div>");
    }

    public HttpSession getApplicationSession(HttpServletRequest request)
    {
        HttpSession appSession = (HttpSession)request.getAttribute(SERVLET_PORTLET_APPLICATION_SESSION);
        if ( appSession == null )
        {
            appSession = request.getSession();
        }
        return appSession;
    }
}
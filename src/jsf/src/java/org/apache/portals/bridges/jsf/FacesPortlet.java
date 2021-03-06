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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;
import javax.faces.webapp.FacesServlet;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.common.GenericServletPortlet;

/**
 * <p>
 * FacesPortlet utilizes Java Server Faces to create the user interface in a
 * portlet environment.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@yahoo.com">David Le Strat</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public class FacesPortlet extends GenericServletPortlet
{

    /** The Log instance for this class. */
    private static final Log log = LogFactory.getLog(FacesPortlet.class);

    /**
     * The VIEW_ROOT used to keep track of action between the action request and
     * the render request.
     */
    public static final String VIEW_ROOT = "org.apache.portals.bridges.jsf.VIEW_ROOT";

    /**
     * The REQUEST_SERVLET_PATH used for
     * externalContext.getRequestServletPath().
     * externalContext.getRequestServletPath() should return null but this is a
     * work around an issue with MyFaces JspViewHandler implementation
     * getServletMapping().
     */
    public static final String REQUEST_SERVLET_PATH = "org.apache.portals.bridges.jsf.REQUEST_SERVLET_PATH";

    /**
     * The REQUEST_TYPE request attribute can be used to determine the Portlet
     * request type ({@link #ACTION_REQUEST}, {@link #VIEW_REQUEST},
     * {@link #HELP_REQUEST}, {@link #EDIT_REQUEST} or {@link #CUSTOM_REQUEST})
     * of the current request.
     */
    public static final String REQUEST_TYPE = "org.apache.portals.bridges.jsf.request_type";

    /** The JSF_VIEW_ID used to maintain the state of the view action. */
    public static final String JSF_VIEW_ID = "jsf_viewid";

    public static final String JSF_EDIT_ID = "jsf_editid";

    public static final String JSF_HELP_ID = "jsf_helpid";

    public static final String JSF_CUSTOM_ID = "jsf_customid";

    /** Name of portlet preference for Action page. */
    public static final String PARAM_ACTION_PAGE = "ActionPage";

    /** Name of portlet preference for Custom page. */
    public static final String PARAM_CUSTOM_PAGE = "CustomPage";

    /** Name of portlet preference for Edit page. */
    public static final String PARAM_EDIT_PAGE = "EditPage";

    /** Name of portlet preference for Edit page */
    public static final String PARAM_HELP_PAGE = "HelpPage";

    /** Name of portlet preference for View page */
    public static final String PARAM_VIEW_PAGE = "ViewPage";

    /** Action request. */
    public static final String ACTION_REQUEST = "ACTION";

    /** View request. */
    public static final String VIEW_REQUEST = "VIEW";

    /** Custom request. */
    public static final String CUSTOM_REQUEST = "CUSTOM";

    /** Edit request. */
    public static final String EDIT_REQUEST = "EDIT";

    /** Help request. */
    public static final String HELP_REQUEST = "HELP";

    /** FacesMessage objects on portlet session */
    public static final String FACES_MESSAGES = "FACES_MESSAGES";

    /** Override default behavior for Unique IDS */
    public static final String PARAM_UNIQUE_IDS = "OverrideUniqueIds";

    /** Default URL for the action page. */
    private String defaultActionPage = null;

    /** Default URL for the custom page. */
    private String defaultCustomPage = null;

    /** Default URL for the edit page. */
    private String defaultEditPage = null;

    /** Default URL for the help page. */
    private String defaultHelpPage = null;

    /** Default URL for the view page. */
    private String defaultViewPage = null;

    private String uniqueIds = null;

    /**
     * <p>
     * Context initialization parameter name for the lifecycle identifier of the
     * {@link Lifecycle}instance to be utilized.
     * </p>
     */
    private static final String LIFECYCLE_ID_ATTR = FacesServlet.LIFECYCLE_ID_ATTR;

    /**
     * <p>
     * The {@link Application}instance for this web application.
     * </p>
     */
    private Application application = null;

    /**
     * <p>
     * Factory for {@link FacesContext}instances.
     * </p>
     */
    private FacesContextFactory facesContextFactory = null;

    /**
     * <p>
     * The {@link Lifecycle}instance to use for request processing.
     * </p>
     */
    private Lifecycle lifecycle = null;

    /**
     * <p>
     * The <code>PortletConfig</code> instance for this portlet.
     * </p>
     */
    private PortletConfig portletConfig = null;

    /**
     * <p>
     * Release all resources acquired at startup time.
     * </p>
     */
    public void destroy()
    {
        if (log.isTraceEnabled())
        {
            log.trace("Begin FacesPortlet.destory() ");
        }
        application = null;
        facesContextFactory = null;
        lifecycle = null;
        portletConfig = null;
        if (log.isTraceEnabled())
        {
            log.trace("End FacesPortlet.destory() ");
        }

    }

    /**
     * <p>
     * Acquire the factory instance we will require.
     * </p>
     * 
     * @exception PortletException
     *                if, for any reason, the startp of this Faces application
     *                failed. This includes errors in the config file that is
     *                parsed before or during the processing of this
     *                <code>init()</code> method.
     */
    public void init(PortletConfig portletConfig) throws PortletException
    {

        if (log.isTraceEnabled())
        {
            log.trace("Begin FacesPortlet.init() ");
        }

        super.init(portletConfig);

        // Save our PortletConfig instance
        this.portletConfig = portletConfig;
        this.defaultViewPage = portletConfig.getInitParameter(PARAM_VIEW_PAGE);
        this.defaultEditPage = portletConfig.getInitParameter(PARAM_EDIT_PAGE);
        this.defaultHelpPage = portletConfig.getInitParameter(PARAM_HELP_PAGE);
        this.uniqueIds = portletConfig.getInitParameter(PARAM_UNIQUE_IDS);

        if (null == this.defaultViewPage)
        {
            // A Faces Portlet is required to have at least the
            // defaultViewPage
            // defined!
            throw new PortletException(
                    "Portlet "
                            + portletConfig.getPortletName()
                            + " is incorrectly configured. No default View page is defined.");
        }
        if (null == this.defaultActionPage)
        {
            this.defaultActionPage = this.defaultViewPage;
        }
        if (null == this.defaultCustomPage)
        {
            this.defaultCustomPage = this.defaultViewPage;
        }
        if (null == this.defaultHelpPage)
        {
            this.defaultHelpPage = this.defaultViewPage;
        }
        if (null == this.defaultEditPage)
        {
            this.defaultEditPage = this.defaultViewPage;
        }
        if (log.isTraceEnabled())
        {
            log.trace("End FacesPortlet.init() ");
        }
    }

    /**
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        process(request, response, defaultEditPage, FacesPortlet.EDIT_REQUEST,
                JSF_EDIT_ID);
    }

    /**
     * @see javax.portlet.GenericPortlet#doHelp(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        if (this.defaultHelpPage != null
                && this.defaultHelpPage.endsWith(".html"))
        {
            super.doHelp(request, response);
        } else
        {
            process(request, response, defaultHelpPage,
                    FacesPortlet.HELP_REQUEST, JSF_HELP_ID);
        }
    }

    /**
     * @param request
     *            The {@link RenderRequest}.
     * @param response
     *            The {@link RenderResponse}.
     * @throws PortletException
     *             Throws a {@link PortletException}.
     * @throws IOException
     *             Throws a {@link IOException}.
     */
    public void doCustom(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        process(request, response, defaultCustomPage,
                FacesPortlet.CUSTOM_REQUEST, JSF_CUSTOM_ID);
    }

    /**
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        process(request, response, defaultViewPage, FacesPortlet.VIEW_REQUEST,
                JSF_VIEW_ID);
    }

    /**
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException
    {
        String viewId = JSF_CUSTOM_ID;
        if (request.getPortletMode().equals(PortletMode.VIEW))
        {
            viewId = JSF_VIEW_ID;
        } else if (request.getPortletMode().equals(PortletMode.EDIT))
        {
            viewId = JSF_EDIT_ID;
        } else if (request.getPortletMode().equals(PortletMode.HELP))
        {
            viewId = JSF_HELP_ID;
        }
        process(request, response, defaultActionPage,
                FacesPortlet.ACTION_REQUEST, viewId);
    }

    /**
     * <p>
     * Gets the {@link FacesContextFactory}.
     * </p>
     * 
     * @return The {@link FacesContextFactory}.
     * @throws PortletException
     *             Throws a {@link PortletException}.
     */
    public FacesContextFactory getFacesContextFactory() throws PortletException
    {
        if (facesContextFactory != null) { return facesContextFactory; }
        try
        {
            facesContextFactory = (FacesContextFactory) FactoryFinder
                    .getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            if (log.isTraceEnabled())
            {
                log.trace("Retrieved facesContextFactory "
                        + facesContextFactory);
            }
        } catch (FacesException e)
        {
            Throwable rootCause = e.getCause();
            if (rootCause == null)
            {
                throw e;
            } else
            {
                throw new PortletException(e.getMessage(), rootCause);
            }
        }
        return facesContextFactory;
    }

    /**
     * <p>
     * Get the faces life cycle.
     * </p>
     * 
     * @return The {@link Lifecycle}.
     * @throws PortletException
     *             Throws a {@link PortletException}.
     */
    public Lifecycle getLifecycle() throws PortletException
    {
        if (lifecycle != null) { return lifecycle; }
        try
        {
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                    .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            if (log.isTraceEnabled())
            {
                log.trace("Retrieved lifecycleFactory " + lifecycleFactory);
            }
            String lifecycleId = portletConfig.getPortletContext()
                    .getInitParameter(LIFECYCLE_ID_ATTR);
            if (log.isDebugEnabled())
            {
                log.debug("lifecycleId " + lifecycleId);
            }
            if (lifecycleId == null)
            {
                lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
            lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
            if (log.isTraceEnabled())
            {
                log.trace("Retrieved lifecycle from lifecycleFactory "
                        + lifecycle);
            }
        } catch (FacesException e)
        {
            Throwable rootCause = e.getCause();
            if (rootCause == null)
            {
                throw e;
            } else
            {
                throw new PortletException(e.getMessage(), rootCause);
            }
        }
        return lifecycle;
    }

    /**
     * <p>
     * Processes the request.
     * </p>
     * 
     * @param request
     *            The {@link PortletRequest}.
     * @param response
     *            The {@link PortletResponse}.
     * @param defaultPage
     *            The default page.
     * @param requestType
     *            The request type.
     * @throws PortletException
     *             Throws a {@link PortletException}.
     * @throws IOException
     *             Throws an {@link IOException}.
     */
    private void process(PortletRequest request, PortletResponse response,
            String defaultPage, String requestType, String viewId)
            throws PortletException, IOException
    {
        boolean actionRequest = ACTION_REQUEST.equals(requestType);
        boolean renderRequest = !actionRequest;
        String defaultView = defaultPage;

        request.setAttribute(REQUEST_TYPE, requestType);

        if (actionRequest)
        {
            log.trace("Begin FacesPortlet.processAction()");
        }

        // remove any lingering JSF attributes
        cleanUpAfterPortal(request, response);

        // Acquire the FacesContext instance for this request
        FacesContext context = getFacesContextFactory().getFacesContext(
                portletConfig.getPortletContext(), request, response,
                getLifecycle());

        // Restore view if available.
        setDefaultView(context, defaultPage, viewId);
        if (log.isTraceEnabled())
        {
            log.trace("Begin Executing phases");
        }

        preProcessFaces(context);

        // Execute the pre-render request processing lifecycle for this request
        try
        {
            if (actionRequest)
            {
                String vi = context.getViewRoot().getViewId();
                context.getApplication().getViewHandler().restoreView(context, vi);
                getLifecycle().execute(context);
                if (log.isTraceEnabled())
                {
                    log.trace("End Executing phases");
                }
                // The view should have been restore.
                // Pass it to the render request.
                request.getPortletSession().setAttribute(
                        createViewRootKey(context, defaultPage, viewId),
                        context.getViewRoot());
                ActionResponse actionResponse = (ActionResponse) response;

                // save FacesMessage objects on session; so they can
                // be restored during the render phase
                saveFacesMessages(context, request.getPortletSession());
            } else if (renderRequest)
            {
                // getLifecycle().execute(context);
                String vi = context.getViewRoot().getViewId();
                context.getApplication().getViewHandler().restoreView(context,
                        vi);

                // restore FacesMessage objects that were previously established
                // in the lifecycle
                restoreFacesMessages(context, request.getPortletSession());

                getLifecycle().render(context);
                if (log.isTraceEnabled())
                {
                    log.trace("End executing RenderResponse phase ");
                }
            } else
            {
                throw new PortletException(
                        "Request must be of type ActionRequest or RenderRequest");
            }

            request.getPortletSession().setAttribute(viewId,
                    context.getViewRoot().getViewId(),
                    PortletSession.PORTLET_SCOPE);

        } catch (FacesException e)
        {
            Throwable t = ((FacesException) e).getCause();
            if (t == null)
            {
                throw new PortletException(e.getMessage(), e);
            } else
            {
                if (t instanceof PortletException)
                {
                    throw ((PortletException) t);
                } else if (t instanceof IOException)
                {
                    throw ((IOException) t);
                } else
                {
                    throw new PortletException(t.getMessage(), t);
                }
            }
        } finally
        {
            // Release the FacesContext instance for this request
            context.release();
        }

        if (log.isTraceEnabled())
        {
            log.trace("End FacesPortlet.process()");
        }
    }

    protected void preProcessFaces(FacesContext context)
    {
    }

    private String createViewRootKey(FacesContext context, String defaultView,
            String viewId)
    {
        PortletRequest portletRequest = (PortletRequest) context
                .getExternalContext().getRequest();
        // String view = portletRequest.getParameter(viewId);
        String view = (String) portletRequest.getPortletSession().getAttribute(
                viewId, PortletSession.PORTLET_SCOPE);

        if (view == null)
        {
            view = defaultView;
        }
        String key = VIEW_ROOT + ":" + getPortletName();
        UIViewRoot root = context.getViewRoot();
        if (root != null)
        {
            key = key + ":" + root.getViewId();
        } else
        {
            key = key + ":" + view;
        }
        if (uniqueIds != null)
        {
            PortletResponse response = (PortletResponse) context
                    .getExternalContext().getResponse();
            if (!(response instanceof RenderResponse))
            {
                log.error("Cant encode action response");
            } else
            {
                RenderResponse rr = (RenderResponse) response;
                key = key + rr.getNamespace();
            }
        }
        return key;
    }

    /**
     * <p>
     * Set the view identifier to the view for the page to be rendered.
     * </p>
     * 
     * @param context
     *            The {@link FacesContext}for the current request.
     * @param defaultView
     *            The default view identifier.
     * @return The default view.
     */
    private void setDefaultView(FacesContext facesContext, String defaultView,
            String viewId)
    {
        // Need to be able to transport viewId between actionRequest and
        // renderRequest.
        PortletRequest portletRequest = (PortletRequest) facesContext
                .getExternalContext().getRequest();
        if (portletRequest instanceof ActionRequest)
        {
            String view = (String) portletRequest.getPortletSession()
                    .getAttribute(viewId, PortletSession.PORTLET_SCOPE);

            if ((null != facesContext.getViewRoot())
                    && (null != facesContext.getViewRoot().getViewId()))
            {
                defaultView = facesContext.getViewRoot().getViewId();
            }
            // else if (null != portletRequest.getParameter(viewId))
            else if (null != view)
            {
                // defaultView = portletRequest.getParameter(viewId);
                defaultView = view;
            }

            UIViewRoot viewRoot = (UIViewRoot) portletRequest
                    .getPortletSession()
                    .getAttribute(
                            createViewRootKey(facesContext, defaultView, viewId));
            if (viewRoot != null)
            {
                facesContext.setViewRoot(viewRoot);
                defaultView = facesContext.getViewRoot().getViewId();
            } else
            {
                facesContext.setViewRoot(new PortletUIViewRoot());
                facesContext.getViewRoot().setViewId(view);
                String defaultRenderKitId = facesContext.getApplication().getDefaultRenderKitId();
                facesContext.getViewRoot().setRenderKitId(defaultRenderKitId != null ? defaultRenderKitId : RenderKitFactory.HTML_BASIC_RENDER_KIT);
                portletRequest.getPortletSession().setAttribute(
                        createViewRootKey(facesContext, view, viewId),
                        facesContext.getViewRoot());
            }
            portletRequest.setAttribute(REQUEST_SERVLET_PATH, defaultView
                    .replaceAll("[.]jsp", ".jsf"));
        } else if (portletRequest instanceof RenderRequest)
        {
            // String view = portletRequest.getParameter(viewId);
            String view = (String) portletRequest.getPortletSession()
                    .getAttribute(viewId, PortletSession.PORTLET_SCOPE);

            if (null == facesContext.getViewRoot())
            {
                if (view == null)
                {
                    view = defaultView;
                }
                UIViewRoot viewRoot = (UIViewRoot) portletRequest
                        .getPortletSession().getAttribute(
                                createViewRootKey(facesContext, view, viewId));
                if (null != viewRoot)
                {
                    facesContext.setViewRoot(viewRoot);
                    defaultView = facesContext.getViewRoot().getViewId();
                } else
                {
                    facesContext.setViewRoot(new PortletUIViewRoot());
                    facesContext.getViewRoot().setViewId(view);
                    String defaultRenderKitId = facesContext.getApplication().getDefaultRenderKitId();
                    facesContext.getViewRoot().setRenderKitId(defaultRenderKitId != null ? defaultRenderKitId : RenderKitFactory.HTML_BASIC_RENDER_KIT);
                    portletRequest.getPortletSession().setAttribute(
                            createViewRootKey(facesContext, view, viewId),
                            facesContext.getViewRoot());
                }
            }
            portletRequest.setAttribute(REQUEST_SERVLET_PATH, view.replaceAll(
                    ".jsp", ".jsf"));
        }
    }

    /**
     * Save FacesMessage objects on the PortletSession
     * 
     * @param context
     * @param session
     */
    private void saveFacesMessages(FacesContext context, PortletSession session)
    {
        Iterator msgs = context.getMessages();
        if (msgs != null && msgs.hasNext())
        {
            Map facesMsgs = new HashMap();

            // messages that have a clientId
            Iterator idsWithMsgs = context.getClientIdsWithMessages();
            while (idsWithMsgs.hasNext())
            {
                String clientId = (String) idsWithMsgs.next();
                List clientMsgList = (List) facesMsgs.get(clientId);
                if (clientMsgList == null)
                {
                    clientMsgList = new ArrayList();
                    facesMsgs.put(clientId, clientMsgList);
                }

                Iterator clientMsgs = context.getMessages(clientId);
                while (clientMsgs != null && clientMsgs.hasNext())
                {
                    clientMsgList.add(clientMsgs.next());
                }
            }

            // messages that do not have a clientId
            Iterator msgsWithoutId = context.getMessages(null);
            if (msgsWithoutId != null && msgsWithoutId.hasNext())
            {
                List msgWithoutIdList = new ArrayList();
                while (msgsWithoutId.hasNext())
                {
                    msgWithoutIdList.add(msgsWithoutId.next());
                }

                facesMsgs.put("null", msgWithoutIdList);
            }

            // save messages on session
            session.setAttribute(FACES_MESSAGES, facesMsgs);
        }
    }

    /**
     * Restore FacesMessage objects from the PortletSession
     * 
     * @param context
     * @param session
     */
    private void restoreFacesMessages(FacesContext context,
            PortletSession session)
    {
        Map facesMsgs = (Map) session.getAttribute(FACES_MESSAGES);

        if (facesMsgs != null)
        {
            for (Iterator clientIds = facesMsgs.keySet().iterator(); clientIds
                    .hasNext();)
            {
                String clientId = (String) clientIds.next();
                List clientMsgList = (List) facesMsgs.get(clientId);

                // did not originally have a clientId specified
                if (clientId.equals("null"))
                {
                    clientId = null;
                }

                // restore messages on the FacesContext
                for (int index = 0; index < clientMsgList.size(); ++index)
                {
                    FacesMessage msg = (FacesMessage) clientMsgList.get(index);
                    context.addMessage(clientId, msg);
                }

                clientMsgList.clear();
            }

            facesMsgs.clear();
            session.setAttribute(FACES_MESSAGES, null);
        }
    }

    /**
     * Removes temporary JSF attributes from the request.
     * 
     * Under certain circumstances, internal JSF attributes from one portlet become
     * available to another portlet on the same page (this can happen, for example, 
     * when first portlet throws an exception while rendering). If this happens,
     * a portlet would not render correctly. 
     * 
     * Theoretically, Portlet server should make sure that no request attributes
     * from one portlet are visible to another portlet. In practice this isn't
     * always the case, so a portlet needs to remove those request attributes
     * before doing anything else. 
     * 
     * @param request portlet request we are processing
     * @param response portlet response we are processing
     */
    protected void cleanUpAfterPortal(PortletRequest request,
            PortletResponse response)
    {
        if (request != null)
        {
            request.removeAttribute("javax.faces.webapp.COMPONENT_TAG_STACK");
            request.removeAttribute("javax.faces.webapp.CURRENT_VIEW_ROOT");
            request.removeAttribute("javax.faces.webapp.CURRENT_FACES_CONTEXT");
        }
    }
}

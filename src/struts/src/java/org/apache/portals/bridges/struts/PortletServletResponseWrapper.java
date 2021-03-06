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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * PortletServletResponseWrapper
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletServletResponseWrapper.java 517068 2007-03-12 01:44:37Z ate $
 */
public class PortletServletResponseWrapper extends HttpServletResponseWrapper
{
    private static final Log log = LogFactory
            .getLog(PortletServletResponseWrapper.class);
    private HttpServletRequest request;
    private boolean actionResponse;
    public PortletServletResponseWrapper(HttpServletRequest request,
            HttpServletResponse response)
    {
        super(response);
        this.request = request;
        this.actionResponse = request.getAttribute(StrutsPortlet.REQUEST_TYPE)
                .equals(StrutsPortlet.ACTION_REQUEST);
    }
    public String encodeURL(String path)
    {
        if (actionResponse)
            return path;
        else
            return super.encodeURL(path);
    }
    public String encodeRedirectURL(String path)
    {
        return path;
    }
    public String encodeUrl(String path)
    {
        if (actionResponse)
            return path;
        else
            return super.encodeUrl(path);
    }
    public String encodeRedirectUrl(String path)
    {
        return path;
    }
    public void sendError(int errorCode, String errorMessage)
            throws IOException
    {
        StrutsPortletErrorContext errorContext = (StrutsPortletErrorContext) request
                .getAttribute(StrutsPortlet.ERROR_CONTEXT);
        if (errorContext == null)
        {
            errorContext = new StrutsPortletErrorContext();
            request.setAttribute(StrutsPortlet.ERROR_CONTEXT, errorContext);
        }
        errorContext.setErrorCode(errorCode);
        errorContext.setErrorMessage(errorMessage);
        errorContext.setError(null);
    }
    public void sendError(int errorCode) throws IOException
    {
        sendError(errorCode, null);
    }
    public void sendRedirect(String path) throws IOException
    {
        if (request.getAttribute(StrutsPortlet.REDIRECT_URL) != null)
        {
            return;
        }
        if (path.startsWith("http://") || path.startsWith("https://"))
        {
            request.setAttribute(StrutsPortlet.REDIRECT_URL, path);
        }
        else
        {
            String contextPath = request.getContextPath();

            // context targeted url captured as Struts Page URL
            if (path.startsWith(contextPath+"/"))
            {
                request.setAttribute(StrutsPortlet.REDIRECT_PAGE_URL, path
                        .substring(contextPath.length()));
            }
            // servlet container root url relative url NOT targetted at the Struts App
            else if ( path.startsWith("/"))
            {
                request.setAttribute(StrutsPortlet.REDIRECT_URL, path);
            }
            // context relative url captured as Struts Page URL
            else
            {
                // TODO: I think this should be translated as relative to current Struts PAGE_URL
                request.setAttribute(StrutsPortlet.REDIRECT_PAGE_URL, path);
            }
        }
    }
}
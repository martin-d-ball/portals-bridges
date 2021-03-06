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
package org.apache.portals.bridges.struts.taglib;

import javax.servlet.ServletRequest; // javadoc
import javax.servlet.jsp.JspException;

import org.apache.portals.bridges.struts.PortletServlet;

/**
 * Supports the Struts html:img tag to be used within a Portlet context allowing relative
 * img src paths.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: ImgTag.java 517068 2007-03-12 01:44:37Z ate $
 */
public class ImgTag extends org.apache.struts.taglib.html.ImgTag 
{
    /**
     * Allow a relative img src path to be used within a PortletRequest context.
     * <p>
     * Temporarily modifies the {@link #src} attribute value (if defined and if within the
     * context of a {@link PortletServlet#isPortletRequest(ServletRequest) PortletRequest}).<br/>
     * A relative src path is changed to a context relative path using the current
     * request uri.</p>
     * @return {@link #EVAL_PAGE}
     */
    public int doEndTag() throws JspException
    {
        String src = getSrc();
        if ( PortletServlet.isPortletRequest(pageContext.getRequest()) && src != null ) 
        {
            try
            {
                setSrc(TagsSupport.getContextRelativeURL(pageContext,src,true));
                super.doEndTag();
            }
            finally
            {
                setSrc(src);
            }
        }
        else
        {
            super.doEndTag();
        }
        return (EVAL_PAGE);
    }
}

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

import javax.servlet.ServletRequest;

import org.apache.portals.bridges.struts.PortletServlet;

/**
 * Supports the Struts html-el:form tag to be used within a Portlet context.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: ELFormTag.java 517068 2007-03-12 01:44:37Z ate $
 */
public class ELFormTag extends org.apache.strutsel.taglib.html.ELFormTag
{
    /**
     * Modifies the default generated form action url to be a valid Portlet ActionURL
     * when in the context of a {@link PortletServlet#isPortletRequest(ServletRequest) PortletRequest}.
     * @return the formStartElement
     */
    protected String renderFormStartElement()
    {
        if ( PortletServlet.isPortletRequest(pageContext.getRequest() ))
        {
            return TagsSupport.getFormTagRenderFormStartElement(pageContext,super.renderFormStartElement());
        }
        else
        {
            return super.renderFormStartElement();
        }
    }
}

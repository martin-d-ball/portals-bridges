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
package org.apache.portals.bridges.common;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ServletContextProvider
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: ServletContextProvider.java 517068 2007-03-12 01:44:37Z ate $
 */
public interface ServletContextProvider 
{
    ServletContext getServletContext(GenericPortlet portlet);

    HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request);

    HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletResponse response);
}

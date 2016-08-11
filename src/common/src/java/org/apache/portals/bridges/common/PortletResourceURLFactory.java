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

import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * A PortletResourceURLFactory can be used to generate an url for direct rendering of a Portlet, comparable (but more limited) 
 * to what Portlet 2.0 (likely) is going to provide when it introduces ResourceURLs.
 * <p>
 * In a Portlet API 1.0 environment one is required to use portal specific extensions for directly rendering a Portlet.
 * </p>
 * <p>
 * This interface can be implemented by portals which provide this type of extension to allow portal agnostic usage of it.
 * Implementation classsz are required to provide a default constructor and only depend on the parameters provided on the
 * {@link createResourceURL(PortletConfig, RenderRequest, RenderResponse, Map) createResourceURL} method.
 * </p>
 * <p>
 * It is expected that portals will implement this interface as a RenderURL.
 * </p>
 * <p>
 * Note: this interface will be defined obsolete as soon as Portlet API 2.0 is available and this functionality then is provided natively.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletResourceURLFactory.java 543153 2007-05-31 15:11:49Z ate $
 */
public interface PortletResourceURLFactory
{
    public String createResourceURL(PortletConfig config, RenderRequest request, RenderResponse response, Map parameters) throws PortletException;
}

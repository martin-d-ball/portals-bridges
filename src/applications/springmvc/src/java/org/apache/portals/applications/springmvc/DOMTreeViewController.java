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
package org.apache.portals.applications.springmvc;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.context.PortletConfigAware;

public class DOMTreeViewController extends AbstractController implements InitializingBean, PortletConfigAware
{	
    private static final Log        log = LogFactory.getLog( DOMTreeViewController.class);

    private DOMTreeService domTreeService ;
    private PortletConfig portletConfig ;
    
    private String xmlFilePath = null ;
    private String xmlFileName = null ;
    
    public void afterPropertiesSet() throws Exception {
        if (this.domTreeService == null)
            throw new IllegalArgumentException("A DOMTreeService is required");
    }
    
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception
	{
		List addTo = new ArrayList();
		if ( getXmlFilePath() != null )
		{
			addTo.add( new DOMTree( getXmlFileName(), getXmlFilePath() ) );
		}
		SortedSet domTreeSet = domTreeService.parseAllDOMTrees( request, this.getPortletContext(), addTo );	
		
		Map model = new HashMap();
		model.put( "messages", portletConfig.getResourceBundle( request.getLocale() ) );
		model.put( "domTreeList", domTreeSet );
		model.put( "domNodeHelper", new DOMTreeService.DOMNodeHelper() );
		
        return new ModelAndView("domTreeView", "model", model);
	}	
	
    public void setDomTreeService(DOMTreeService domTreeService)
    {
        this.domTreeService = domTreeService;
    }
    
    public String getXmlFilePath()
    {
        return this.xmlFilePath ;
    }
    public void setXmlFilePath(String xmlFile)
    {
        this.xmlFilePath = xmlFile;
    }
    public String getXmlFileName()
    {
        return this.xmlFileName;
    }
    public void setXmlFileName(String xmlFileName)
    {
        this.xmlFileName = xmlFileName;
    }
    public void setPortletConfig(PortletConfig portletConfig)
    {
    	
        this.portletConfig = portletConfig;
    }
}

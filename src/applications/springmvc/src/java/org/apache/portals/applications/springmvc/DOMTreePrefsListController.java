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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.mvc.AbstractController;
import org.springframework.web.portlet.ModelAndView;

public class DOMTreePrefsListController extends AbstractController implements InitializingBean
{
	private DOMTreeService domTreeService;
    
    public void afterPropertiesSet() throws Exception
    {
        if (this.domTreeService == null)
            throw new IllegalArgumentException( "A DOMTreeService is required" );
    }
    
	public ModelAndView handleRenderRequestInternal( RenderRequest request, RenderResponse response ) throws Exception
	{
		Map model = new HashMap();
		model.put( "list", domTreeService.getAllDOMTrees( request ) );
        return new ModelAndView( "domTreeEditList", "model", model );
	}

	public void setDomTreeService( DOMTreeService domTreeService )
    {
        this.domTreeService = domTreeService;
    }
}

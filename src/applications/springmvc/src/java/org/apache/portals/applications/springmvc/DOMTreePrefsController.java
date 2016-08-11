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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.validation.BindException;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.mvc.SimpleFormController;
import org.springframework.web.portlet.ModelAndView;

import org.apache.portals.applications.springmvc.DOMTree;

public class DOMTreePrefsController extends SimpleFormController implements InitializingBean {

    private DOMTreeService domTreeService;
    
    public void afterPropertiesSet() throws Exception
    {
        if ( this.domTreeService == null )
            throw new IllegalArgumentException( "DOMTreeService is required" );
    }

	public void onSubmitAction( ActionRequest request, ActionResponse response,
			Object command,	BindException errors ) throws Exception
	{	
        String save = request.getParameter( "save" );
        if ( save != null )
        {
        	domTreeService.saveDOMTree( request.getParameter("name"), request.getParameter("path"), request );
        }
		response.setRenderParameter( "action", "list" );
	}
	
	protected Object formBackingObject( PortletRequest request ) throws Exception
	{
		String name = request.getParameter( "domTree" );
		if ( name == null )
		{
			return new DOMTree();
		}
		else
		{
			return domTreeService.getDOMTree( name, request );
		}
	}
    
	protected void initBinder(PortletRequest request, PortletRequestDataBinder binder)
			throws Exception
    {
	    //SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	    //binder.registerCustomEditor(Date.class, null, new	CustomDateEditor(dateFormat, true));
	    //binder.setAllowedFields(new String[] {"author","title","description","availability","count"});
	}

	protected ModelAndView renderInvalidSubmit(RenderRequest request, RenderResponse response)
			throws Exception
	{
	    return null;
	}
	
	protected void handleInvalidSubmit(ActionRequest request, ActionResponse response)
			throws Exception
	{
		response.setRenderParameter( "action","view" );
	}

    public void setDomTreeService(DOMTreeService domTreeService)
    {
        this.domTreeService = domTreeService;
    }
}

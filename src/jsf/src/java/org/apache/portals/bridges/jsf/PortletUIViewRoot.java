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

import javax.faces.component.NamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.portlet.RenderResponse;

/**
 * A portlet view root that implements a naming container to creates unique 
 * client ids.
 * @author Matthew Bruzek
 */
public class PortletUIViewRoot extends UIViewRoot implements NamingContainer
{
    /** A portlet view id constant to prepend to the namespace. */
    public static final String VIEW_PREFIX = "view";

    private String _namespace;

    /** The default constructor calls the UIViewRoot default constructor.  */
    public PortletUIViewRoot()
    {
        super();
    }

    /**
     * The convenience constructor creates a PortletUIViewRoot from a UIViewRoot.
     * @param viewRoot The UIViewRoot to use when creating this object.
     */
    public PortletUIViewRoot( UIViewRoot viewRoot )
    {
        setLocale( viewRoot.getLocale() );
        setRenderKitId( viewRoot.getRenderKitId() );
        setViewId( viewRoot.getViewId() );
    }

    /**
     * Return a string which can be used as output to the response which uniquely
     * identifies a component within the current view.
     * @param context The FacesContext object for the current request.
     */
    public String getClientId( FacesContext context )
    {
        if ( context == null )
            throw new NullPointerException( "context can not be null." );

        if (_namespace == null && context.getExternalContext().getResponse() instanceof RenderResponse) {
            String nameSpace = context.getExternalContext().encodeNamespace( "" );
            _namespace = VIEW_PREFIX + nameSpace;
        }
        return _namespace;
    }


    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
        values[1] = _namespace;
        return values;
    }


    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[])state;
        super.restoreState(context, values[0]);
        _namespace = (String) values[1];
    }
}


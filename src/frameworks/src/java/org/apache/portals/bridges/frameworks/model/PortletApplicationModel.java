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
package org.apache.portals.bridges.frameworks.model;

import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;


/**
 * PortletApplicationModel
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletApplicationModel.java 517068 2007-03-12 01:44:37Z ate $
 */
public interface PortletApplicationModel
{
    void init(PortletConfig config)
    throws PortletException;
    
    ModelBean getModelBean(String view);

    String getTemplate(String view);
    
    Object createBean(ModelBean mb);
    Object lookupBean(ModelBean mb, String key);
    
    Map createPrefsBean(ModelBean mb, Map prefs);
    
    Map validate(Object bean, String view, ResourceBundle bundle)
    throws PortletException;
        
    String getForward(String view, String status);
    
    String getForward(String actionForward);
    void setExternalSupport(Map map);
}

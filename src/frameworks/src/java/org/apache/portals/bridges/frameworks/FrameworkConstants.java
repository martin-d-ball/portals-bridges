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
package org.apache.portals.bridges.frameworks;


/**
 * FrameworkConstants
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: FrameworkConstants.java 517068 2007-03-12 01:44:37Z ate $
 */
public interface FrameworkConstants
{
    /**
     * Use this post parameter name for your actions
     */
    final String BRIDGES_ACTION = "portlet.action";

    /**
     * The name of the forward tool
     */
    final String FORWARD_TOOL = "forward";
    final String MODEL_TOOL = "bridges.model";
    
    /**
     * The name of the prefs variable
     */
    final String PREFS_VARIABLE = "prefs";
    
    /**
     * Current view for view mode
     */
    final String VIEW_VIEW_MODE = "bridges.view.view";
    
    /**
     * Current view for edit mode
     */
    final String VIEW_EDIT_MODE = "bridges.view.edit";
    
    /**
     * Current view for help mode
     */
    final String VIEW_HELP_MODE = "bridges.view.help";

}

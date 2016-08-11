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
package org.apache.portals.bridges.struts.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public abstract class AbstractConfigComponent
{
    protected static class SetParentRule extends Rule
    {
        private Object parent;
        
        public SetParentRule(Object parent)
        {
            this.parent = parent;
        }
        
        public void begin( String arg0, String arg1, Attributes arg2 ) throws Exception
        {
            digester.push(parent);
        }        
    }
    
    private boolean loaded = false;
    
    public AbstractConfigComponent()
    {
    }
    
    protected void checkLoaded()
    {
        if (loaded)
            throw new IllegalStateException("Already loaded");
    }
    
    public abstract void configure(Digester digester);
    
    public void afterLoad()
    {
        checkLoaded();
        loaded = true;
    }
}
